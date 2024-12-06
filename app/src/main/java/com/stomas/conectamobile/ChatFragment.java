package com.stomas.conectamobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private CollectionReference messagesRef;
    private FirebaseAuth auth;

    // HiveMQ Variables
    private Mqtt5AsyncClient mqttClient;
    private final String MQTT_BROKER_URL = "broker.hivemq.com"; // Cambia según tu broker
    private final int MQTT_BROKER_PORT = 1883;
    private final String MQTT_TOPIC = "conectamobile/chat";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Inicializar Firebase Firestore y Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        messagesRef = db.collection("chats").document("general_chat").collection("messages");

        // Inicializar vistas
        recyclerView = view.findViewById(R.id.recycler_chat);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.btn_send);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);


        // Inicializar MQTT
        initializeMQTT();

        // Cargar mensajes desde Firestore
        loadMessagesFromFirestore();

        // Acción de enviar mensaje
        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void initializeMQTT() {
        mqttClient = Mqtt5Client.builder()
                .identifier("ConectaMobile_" + System.currentTimeMillis())
                .serverHost(MQTT_BROKER_URL)
                .serverPort(MQTT_BROKER_PORT)
                .buildAsync();

        mqttClient.connect()
                .whenComplete((ack, throwable) -> {
                    if (throwable == null) {
                        Toast.makeText(getContext(), "Conectado a HiveMQ", Toast.LENGTH_SHORT).show();
                        subscribeToMQTT();
                    } else {
                        Toast.makeText(getContext(), "Error al conectar con HiveMQ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribeToMQTT() {
        mqttClient.subscribeWith()
                .topicFilter(MQTT_TOPIC)
                .callback(this::onMessageReceived)
                .send()
                .whenComplete((subAck, throwable) -> {
                    if (throwable == null) {
                        Toast.makeText(getContext(), "Suscripción exitosa al tema MQTT", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error al suscribirse al tema MQTT", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onMessageReceived(Mqtt5Publish publish) {
        ByteBuffer payload = publish.getPayload().orElse(null);
        if (payload != null) {
            byte[] bytes = new byte[payload.remaining()];
            payload.get(bytes);
            String messageContent = new String(bytes);

            // Suponiendo que el mensaje tiene el formato: "userId:mensaje"
            String[] parts = messageContent.split(":", 2);
            if (parts.length == 2) {
                String userId = parts[0]; // El userId del remitente
                String messageText = parts[1]; // El contenido del mensaje

                // Consultar Firestore para obtener el correo electrónico del usuario
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String email = documentSnapshot.getString("email");
                            if (email == null) email = "Usuario desconocido";

                            // Crear el objeto Message con el correo recuperado
                            Message message = new Message(userId, email, messageText, System.currentTimeMillis());

                            getActivity().runOnUiThread(() -> {
                                messageList.add(message);
                                chatAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            });
                        })
                        .addOnFailureListener(e -> {
                            // En caso de error, usar un correo predeterminado
                            Message message = new Message(userId, "Error al obtener correo", messageText, System.currentTimeMillis());

                            getActivity().runOnUiThread(() -> {
                                messageList.add(message);
                                chatAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            });
                        });
            }
        }
    }




    private void loadMessagesFromFirestore() {
        messagesRef.orderBy("timestamp").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getContext(), "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
                return;
            }

            messageList.clear();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Convertir cada documento en un objeto Message
                Message message = document.toObject(Message.class);
                messageList.add(message);
            }
            chatAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messageList.size() - 1);
        });
    }



    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        // Publicar mensaje en HiveMQ
        mqttClient.publishWith()
                .topic(MQTT_TOPIC)
                .payload(message.getBytes())
                .send()
                .whenComplete((publish, throwable) -> {
                    if (throwable != null) {
                        Toast.makeText(getContext(), "Error al enviar mensaje MQTT", Toast.LENGTH_SHORT).show();
                    }
                });

        // Obtener el correo del usuario autenticado
        String email = auth.getCurrentUser().getEmail();

        // Guardar mensaje en Firestore
        Message chatMessage = new Message(auth.getCurrentUser().getUid(), email, message, System.currentTimeMillis());
        messagesRef.add(chatMessage).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                messageInput.setText("");
            } else {
                Toast.makeText(getContext(), "Error al guardar mensaje en Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

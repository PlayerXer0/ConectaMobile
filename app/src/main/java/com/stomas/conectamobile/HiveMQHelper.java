package com.stomas.conectamobile;

import android.util.Log;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.nio.charset.StandardCharsets;

public class HiveMQHelper {

    private static final String BROKER_URL = "broker.hivemq.com"; // Dirección del broker MQTT
    private static final int BROKER_PORT = 1883; // Puerto del broker MQTT
    private static final String CLIENT_ID = "ConectaMobileClient"; // ID único para el cliente MQTT

    private Mqtt5AsyncClient mqttClient;

    // Método para inicializar y conectar
    public void connect() {
        try {
            mqttClient = MqttClient.builder()
                    .useMqttVersion5()
                    .identifier(CLIENT_ID) // ID único del cliente
                    .serverHost(BROKER_URL) // Dirección del broker
                    .serverPort(BROKER_PORT)
                    .buildAsync();

            mqttClient.connectWith()
                    .send()
                    .whenComplete((connAck, throwable) -> {
                        if (throwable != null) {
                            Log.e("HiveMQHelper", "Error al conectar al broker MQTT", throwable);
                        } else {
                            Log.d("HiveMQHelper", "Conectado al broker MQTT");
                        }
                    });
        } catch (Exception e) {
            Log.e("HiveMQHelper", "Error al inicializar MQTT", e);
        }
    }

    // Método para suscribirse a un tema
    public void subscribe(String topic, MqttMessageListener listener) {
        if (mqttClient != null && mqttClient.getState().isConnected()) {
            mqttClient.subscribeWith()
                    .topicFilter(topic)
                    .callback(publish -> {
                        String message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
                        listener.onMessageReceived(topic, message);
                    })
                    .send()
                    .whenComplete((subAck, throwable) -> {
                        if (throwable != null) {
                            Log.e("HiveMQHelper", "Error al suscribirse al tema: " + topic, throwable);
                        } else {
                            Log.d("HiveMQHelper", "Suscripción exitosa al tema: " + topic);
                        }
                    });
        } else {
            Log.e("HiveMQHelper", "Cliente MQTT no conectado.");
        }
    }

    // Método para publicar un mensaje
    public void publish(String topic, String message) {
        if (mqttClient != null && mqttClient.getState().isConnected()) {
            mqttClient.publishWith()
                    .topic(topic)
                    .payload(message.getBytes(StandardCharsets.UTF_8))
                    .send()
                    .whenComplete((publish, throwable) -> {
                        if (throwable != null) {
                            Log.e("HiveMQHelper", "Error al publicar mensaje", throwable);
                        } else {
                            Log.d("HiveMQHelper", "Mensaje publicado: " + message);
                        }
                    });
        } else {
            Log.e("HiveMQHelper", "Cliente MQTT no conectado.");
        }
    }

    // Método para desconectar
    public void disconnect() {
        if (mqttClient != null) {
            mqttClient.disconnectWith()
                    .send()
                    .whenComplete((disconnectAck, throwable) -> {
                        if (throwable != null) {
                            Log.e("HiveMQHelper", "Error al desconectar", throwable);
                        } else {
                            Log.d("HiveMQHelper", "Desconectado del broker MQTT");
                        }
                    });
        }
    }

    // Interfaz para manejar mensajes recibidos
    public interface MqttMessageListener {
        void onMessageReceived(String topic, String message);
    }
}

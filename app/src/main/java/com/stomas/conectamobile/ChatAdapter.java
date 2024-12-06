package com.stomas.conectamobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList; // Lista de objetos Message

    // Constructor
    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Obtener el mensaje en la posición actual
        Message message = messageList.get(position);

        // Establecer los datos en las vistas
        holder.emailTextView.setText(message.getEmail()); // Correo del usuario
        holder.messageTextView.setText(message.getMessage()); // Contenido del mensaje
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder interno para el RecyclerView
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            // Referenciar las vistas del layout `item_message.xml`
            emailTextView = itemView.findViewById(R.id.email_text);
            messageTextView = itemView.findViewById(R.id.message_text);
        }
    }
}

package com.hahn.client.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hahn.client.http.OkHttpClientWithCookies;
import com.hahn.client.model.CommentDTO;
import com.hahn.client.model.EmployeeRequest;
import com.hahn.client.model.TicketRequest;
import com.hahn.client.model.TicketResponse;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TicketService {

    private static final String API_URL = "http://localhost:8080";
    private final OkHttpClient client;
    private final Gson gson;

    public TicketService() {
        this.client = OkHttpClientWithCookies.getClient();
        this.gson = new Gson();
    }

    // Récupérer tous les tickets
    public List<TicketResponse> getAllTickets() throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets/all")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                //System.out.println("response.body().string(): "+response.body().string());
                Type listType = new TypeToken<List<TicketResponse>>() {}.getType();
                return gson.fromJson(response.body().string(), listType);
            } else {
                throw new IOException("Échec de la récupération des tickets : " + response.message());
            }
        }
    }

    // Récupérer un ticket par son ID
    public List<TicketResponse> getTicketsByEmployee(EmployeeRequest employee) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets/my")  // Utilise l'endpoint existant pour les tickets de l'employé
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<TicketResponse>>() {}.getType();
                return gson.fromJson(response.body().string(), listType);
            } else {
                throw new IOException("Failed to get employee tickets: " + response.message());
            }
        }
    }

    // Créer un nouveau ticket
    public TicketResponse createTicket(TicketRequest ticketRequest) throws IOException {
        String json = gson.toJson(ticketRequest);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), TicketResponse.class);
            } else {
                throw new IOException("Échec de la création du ticket : " + response.message());
            }
        }
    }


    // Mettre à jour le statut du ticket
    public TicketResponse updateTicketStatus(Long ticketId, String newStatus) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets/" + ticketId + "/status?newStatus=" + newStatus)
                .put(RequestBody.create(null, new byte[0])) // Pas de corps pour cette requête
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), TicketResponse.class);
            } else {
                throw new IOException("Échec de la mise à jour du statut : " + response.message());
            }
        }
    }

    public TicketResponse addComment(Long ticketId, String comment) throws IOException {
        String json = gson.toJson(comment);
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json
        );

        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets/add/" + ticketId + "/comments")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return gson.fromJson(response.body().string(), TicketResponse.class);
            } else {
                throw new IOException("Échec de l'ajout du commentaire : " + response.message());
            }
        }
    }

    public List<CommentDTO> getComments(Long ticketId) throws IOException {
        Request request = new Request.Builder()
                .url(API_URL + "/api/tickets/" + ticketId + "/comments")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Type listType = new TypeToken<List<CommentDTO>>(){}.getType();
                return gson.fromJson(response.body().string(), listType);
            } else {
                throw new IOException("Échec de la récupération des commentaires : " + response.message());
            }
        }
    }


}

package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private static final String API_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    private static final Gson gson = new Gson();
    private final Semaphore semaphore;
    private final long requestTimeoutMillis;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.semaphore = new Semaphore(requestLimit);
        this.requestTimeoutMillis = timeUnit.toMillis(1);
    }
    public void createDocument(CrptJson document, String signature) {
        try {
            semaphore.acquire();
            String jsonPayload = gson.toJson(document);

            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Signature",signature);
            connection.setDoOutput(true);

            try (var writer = connection.getOutputStream()) {
                writer.write(jsonPayload.getBytes());
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    @Setter
    @Getter
    static class CrptJson {
        @JsonProperty("description")
        private Description description;

        @JsonProperty("doc_id")
        private String docId;

        @JsonProperty("doc_status")
        private String docStatus;

        @JsonProperty("doc_type")
        private DocType docType;

        @JsonProperty("importRequest")
        private boolean importRequest;

        @JsonProperty("owner_inn")
        private String ownerInn;

        @JsonProperty("participant_inn")
        private String participantInn;

        @JsonProperty("producer_inn")
        private String producerInn;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("production_date")
        private Date productionDate;

        @JsonProperty("production_type")
        private String productionType;

        @JsonProperty("products")
        private List<Product> products;

        @JsonFormat(pattern = "yyyy-MM-dd")
        @JsonProperty("reg_date")
        private Date regDate;

        @JsonProperty("reg_number")
        private String regNumber;

        @Setter
        @Getter
        private static class Description {
            @JsonProperty("participantInn")
            private String participantInn;
        }
        @Setter
        @Getter
        private static class Product {
            @JsonProperty("certificate_document")
            private String certificateDocument;

            @JsonFormat(pattern = "yyyy-MM-dd")
            @JsonProperty("certificate_document_date")
            private Date certificateDocumentDate;

            @JsonProperty("certificate_document_number")
            private String certificateDocumentNumber;

            @JsonProperty("owner_inn")
            private String ownerInn;

            @JsonProperty("producer_inn")
            private String producerInn;

            @JsonFormat(pattern = "yyyy-MM-dd")
            @JsonProperty("production_date")
            private Date productionDate;

            @JsonProperty("tnved_code")
            private String tnvedCode;

            @JsonProperty("uit_code")
            private String uitCode;

            @JsonProperty("uitu_code")
            private String uituCode;

        }
    }
}

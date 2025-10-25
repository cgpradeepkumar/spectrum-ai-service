package in.learning.spectrum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import in.learning.spectrum.model.Flashcard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class GeminiAiClient {

    private final Client geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MODEL_NAME = "gemini-2.5-flash"; // Excellent for speed/cost

    // The Gemini Client automatically picks up the GEMINI_API_KEY environment variable.
    // If you need to explicitly pass it, you can modify the constructor.
    public GeminiAiClient(@Value("${gemini.api.key}") String apiKey) {
        this.geminiClient = Client.builder().apiKey(apiKey).build();
    }

    public List<Flashcard> generateFlashcards(String context, int count) throws IOException {
        String prompt = buildPrompt(context, count);

        try {
            // Call the generateContent method
            GenerateContentResponse response = geminiClient.models
                    .generateContent(MODEL_NAME, prompt, null);

            String jsonResponse = response.text();

            // Clean the response: LLMs sometimes wrap JSON in markdown fences (```json...```)
            jsonResponse = jsonResponse.replace("```json", "").replace("```", "").trim();

            // Map the JSON string to your List<Flashcard> DTO
            return objectMapper.readValue(jsonResponse, new TypeReference<List<Flashcard>>() {});

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            // It's good practice to return an empty list or throw a custom exception
            return Collections.emptyList();
        }
    }

    // (Prompt construction logic remains the same)
    private String buildPrompt(String context, int count) {
        return "You are an expert educator and study assistant. Your task is to generate flashcards from the provided text."
                + " Identify the most important key concepts, definitions, and facts from the text below."
                + " Generate exactly " + count + " flashcards."
                + " Your output MUST be a valid JSON array of objects, where each object has two keys: 'front' and 'back'."
                + " The 'front' should be a concise term or a direct question."
                + " The 'back' should be the corresponding definition or answer."
                + " Do not include any other text or explanations outside of the JSON array."
                + "\n--- TEXT BEGINS ---\n"
                + context
                + "\n--- TEXT ENDS ---\n"
                + "\nJSON Response:\n";
    }
}

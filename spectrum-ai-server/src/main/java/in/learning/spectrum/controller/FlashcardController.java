package in.learning.spectrum.controller;

import in.learning.spectrum.model.Flashcard;
import in.learning.spectrum.service.FlashcardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private final FlashcardService flashcardService;

    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @PostMapping("/generate")
    public ResponseEntity<List<Flashcard>> generateFlashcards(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "10") int count) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            List<Flashcard> flashcards = flashcardService.generate(file, count);
            return ResponseEntity.ok(flashcards);
        } catch (IOException e) {
            // Log the exception
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Or return an error message
        }
    }

}

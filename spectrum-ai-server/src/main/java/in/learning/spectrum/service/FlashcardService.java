package in.learning.spectrum.service;

import in.learning.spectrum.model.Flashcard;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FlashcardService {

    List<Flashcard> generate(MultipartFile file, int count) throws IOException;
}

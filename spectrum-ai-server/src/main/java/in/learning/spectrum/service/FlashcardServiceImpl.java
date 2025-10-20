package in.learning.spectrum.service;

import in.learning.spectrum.client.GeminiAiClient;
import in.learning.spectrum.model.Flashcard;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service("flashcardService")
public class FlashcardServiceImpl implements FlashcardService {

    @Autowired
    private GeminiAiClient geminiAiClient;

    @Override
    public List<Flashcard> generate(MultipartFile file, int count) throws IOException {
        String text = extractText(file);
        return geminiAiClient.generateFlashcards(text, count);
    }

    private String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        if ("application/pdf".equals(contentType)) {
            PDDocument document = Loader.loadPDF(inputStream.readAllBytes());
            String text = new PDFTextStripper().getText(document);
            document.close();
            return text;
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
            XWPFDocument doc = new XWPFDocument(inputStream);
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            String text = extractor.getText();
            extractor.close();
            return text;
        } else if ("text/plain".equals(contentType)) {
            return new String(inputStream.readAllBytes());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }
}

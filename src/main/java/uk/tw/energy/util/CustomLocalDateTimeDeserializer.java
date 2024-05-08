package uk.tw.energy.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.boot.jackson.JsonComponent;

/** Custom JSON deserializer for LocalDateTime objects. */
@JsonComponent
public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /** Default constructor for CustomLocalDateTimeDeserializer. */
  public CustomLocalDateTimeDeserializer() {
    // Default constructor
  }

  /**
   * Deserializes a JSON string into a LocalDateTime object using the provided JsonParser and
   * DeserializationContext.
   *
   * @param p the JsonParser used to parse the JSON string
   * @param ctxt the DeserializationContext used for deserialization
   * @return the deserialized LocalDateTime object
   * @throws IOException if an I/O error occurs
   * @throws JsonProcessingException if the JSON string cannot be processed
   */
  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    return LocalDateTime.parse(p.getText(), FORMATTER);
  }
}

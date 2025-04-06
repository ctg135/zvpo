package mtusi.zvpo.entites;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "audit")
public class AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    public UUID id;

    @Column(name = "signature_id", nullable = false)
    public UUID signatureId;

    @Column(name = "created_at")
    public Date createdAt;

    @Column(name = "change_type")
    public String changeType;

    @Column(name = "changed_by")
    public String changedBy;

    @Column(name = "fields_changed", length = 1024)
    public String fields_changed;

    public static AuditEntity generateUpdated(
        SignatureEntity oldSignature,
        SignatureEntity newSignature,
        String changedBy){
        // Функция для генерации аудита изменения сигнатуры
        var result = new AuditEntity();

        result.createdAt = new Date();
        result.signatureId = oldSignature.id;
        result.changeType = "UPDATED";
        result.changedBy = changedBy;

        Map<String, String> changedValues = new HashMap<>(Collections.emptyMap());

        ObjectMapper m = new ObjectMapper();
        var oldProps = m.convertValue(oldSignature, Map.class);
        var newProps = m.convertValue(newSignature, Map.class);

        for (Object prop : oldProps.keySet()) {
            String property = (String) prop;
            Object oldProperty = oldProps.get(property);
            Object newProperty = newProps.get(property);
            if (!oldProperty.equals(newProperty)) {
                changedValues.put("old_" + property, oldProperty.toString());
                changedValues.put("new_" + property, newProperty.toString());
            }
        }

        try {
            result.fields_changed = m.writeValueAsString(changedValues);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static AuditEntity generateUpdated(
        SignatureEntity oldSignature,
        SignatureEntity newSignature){
        // Перегруженная функция для генерации аудита по умолчанию
        return generateUpdated(
                oldSignature,
                newSignature,
                "default_user"
        );
    }

    public static AuditEntity generateDeleted(
        SignatureEntity deleted,
        String changedBy){
        // Функция для генерации аудита удаления сигнатуры
        var result = new AuditEntity();

        result.createdAt = new Date();
        result.signatureId = deleted.id;
        result.changeType = "DELETED";
        result.changedBy = changedBy;
        result.fields_changed = "";

        return result;
    }

    public static AuditEntity generateDeleted(
            SignatureEntity deleted){
        // Перегруженная функция для генерации аудита по удалению сигнатуры
        return generateDeleted(
                deleted,
                "default_user"
        );
    }

    public static AuditEntity generateAdded(
            SignatureEntity added,
            String changedBy){
        // Функция для генерации аудита добавления сигнатуры
        var result = new AuditEntity();

        result.createdAt = new Date();
        result.signatureId = added.id;
        result.changeType = "ADDED";
        result.changedBy = changedBy;
        result.fields_changed = "";

        return result;
    }

    public static AuditEntity generateAdded(
            SignatureEntity added){
        // Перегруженная функция для генерации аудита по созданию сигнатуры
        return generateAdded(
                added,
                "default_user");
    }
}

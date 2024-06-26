package fun.ewjx.repo;

import fun.ewjx.entity.ChatMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {

}

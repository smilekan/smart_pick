package smartpick;

import org.springframework.transaction.annotation.Transactional;
import smartpick.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired
    private PickRepository pickRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverDelivered_Receive(@Payload Delivered delivered){

        if(delivered.isMe()){
            System.out.println("##### listener Receive : " + delivered.toJson());
            Pick newPick = new Pick(delivered);
            pickRepository.save(newPick);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    @Transactional
    public void wheneverCanceled_Cancel(@Payload Canceled canceled){

        if(canceled.isMe()){
            Pick cancelPick = pickRepository.findByOrderId(canceled.getId());
            if (cancelPick != null)
                cancelPick.setStatus("CANCELED");
        }
    }

}

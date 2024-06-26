package ScreeningHumanity.stockChatServer.adapter.out.infrastructure.mongo.entity;

import ScreeningHumanity.stockChatServer.application.port.out.dto.StockChatOutDto;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "stock_chat")
public class StockChatEntity {

	@Id
	private String id;
	private String stockCode;
	private String message;
	private String sender;
	private String nickName;
	private Instant createAt;

	@Builder
	public StockChatEntity(String id, String stockCode, String message, String sender, String nickName,
			Instant createAt) {
		this.id = id;
		this.stockCode = stockCode;
		this.message = message;
		this.sender = sender;
		this.nickName = nickName;
		this.createAt = createAt;
	}

	public static StockChatEntity getStockChatOutDto(StockChatOutDto dto) {
		return StockChatEntity.builder()
				.stockCode(dto.getStockCode())
				.message(dto.getMessage())
				.sender(dto.getSender())
				.nickName(dto.getNickName())
				.createAt(dto.getCreateAt())
				.build();
	}

	public static StockChatEntity getReactiveStockChatEntity(org.bson.Document document) {
		return StockChatEntity.builder()
				.id(document.getObjectId("_id").toString())
				.stockCode(document.getString("stockCode"))
				.message(document.getString("message"))
				.sender(document.getString("sender"))
				.nickName(document.getString("nickName"))
				.createAt(document.getDate("createAt").toInstant())
				.build();
	}
}

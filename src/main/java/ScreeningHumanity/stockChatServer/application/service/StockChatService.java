package ScreeningHumanity.stockChatServer.application.service;

import ScreeningHumanity.stockChatServer.application.port.in.dto.StockChatInDto;
import ScreeningHumanity.stockChatServer.application.port.in.usecase.StockChatUseCase;
import ScreeningHumanity.stockChatServer.application.port.out.LoadStockChatPort;
import ScreeningHumanity.stockChatServer.application.port.out.SaveStockChatPort;
import ScreeningHumanity.stockChatServer.application.port.out.dto.StockChatOutDto;
import ScreeningHumanity.stockChatServer.domain.StockChat;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
//@RequiredArgsConstructor
public class StockChatService implements StockChatUseCase {

	private final Sinks.Many<StockChatInDto> stockChatSink;
	private final SaveStockChatPort saveStockChatPort;
	private final LoadStockChatPort loadStockChatPort;

	public StockChatService(
			SaveStockChatPort saveStockChatPort,
			LoadStockChatPort loadStockChatPort) {
		this.stockChatSink = Sinks.many().multicast().onBackpressureBuffer();
		this.saveStockChatPort = saveStockChatPort;
		this.loadStockChatPort = loadStockChatPort;
	}


	@Override
	public Mono<StockChatInDto> sendChat(StockChatInDto dto) {
		return Mono.fromCallable(() -> StockChat.sendChat(dto))
				.flatMap(stockChat -> StockChatInDto.getStockChatOutDto(
						saveStockChatPort.saveStockChat(StockChatOutDto.getStockChat(stockChat))))
				.doOnNext(stockChatSink::tryEmitNext);
	}

	@Override
	public Flux<StockChatInDto> getChats(String stockCode) {
		return StockChatInDto.getStockChats(
						StockChat.getStockChats(loadStockChatPort.getChats(stockCode)))
				.concatWith(stockChatSink.asFlux()
						.filter(stockChat -> stockChat.getStockCode().equals(stockCode)));
	}
}
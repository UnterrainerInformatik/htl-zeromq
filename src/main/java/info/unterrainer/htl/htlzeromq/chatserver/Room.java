package info.unterrainer.htl.htlzeromq.chatserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Builder
@Data
@Accessors(fluent = true)
public class Room {

	private final List<String> messages = new ArrayList<>();
	private final Map<Participant, Integer> participantIndex = new HashMap<>();
}

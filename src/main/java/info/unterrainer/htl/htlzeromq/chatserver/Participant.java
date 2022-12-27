package info.unterrainer.htl.htlzeromq.chatserver;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Builder
@Data
@Accessors(fluent = true)
public class Participant {

	private final String name;
}

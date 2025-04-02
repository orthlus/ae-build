package art.aelaort.utils.system;

import lombok.Builder;

@Builder
public record Response(int exitCode, String stdout, String stderr) {
}

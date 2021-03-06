package ch.mobi.ufi.domain.flight.parameters;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DefaultFlightParameters implements FlightParameters {
	private LocalDate date;
}

package ch.mobi.ufi.domain.price;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.mobi.ufi.domain.flight.entity.Flight;
import ch.mobi.ufi.domain.flight.vo.FlightIdentifier;
import ch.mobi.ufi.domain.risk.predictor.DelayEstimator;
import ch.mobi.ufi.domain.risk.predictor.RiskCoverage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PricingCalculator {
    private static final double K = 1.1;
    private static final double M1 = 0.1;
    private static final double M2 = 5;
    private Map<FlightIdentifier, Double> adaptiveM2 = new HashMap<>();

    @NonNull
    private DelayEstimator delayEstimator;

    public List<RiskCoverage> getRiskCoverages(Flight flight, Integer minDelay) {
        List<RiskCoverage> riskCoverages = new ArrayList<>();
        riskCoverages.add(buildRiskCoverage(flight, minDelay, "Bronze", 500));
        riskCoverages.add(buildRiskCoverage(flight, minDelay, "Silver", 2_000));
        riskCoverages.add(buildRiskCoverage(flight, minDelay, "Gold", 8_000));
        LOG.info("coverage for {}, minDelay={}: {}", flight, minDelay, riskCoverages);
        return riskCoverages;
    }

    /**
     * Creates a risk coverage for a flight. The coverage is available only
     * if it makes sense (e.g. the flight has not yet landed and the
     * premium to pay is lower than the insured amount).
     *
     * @param flight
     * @param minDelay
     * @param coverageName  the coverage name (e.g. basic/medium/ultimate)
     * @param insuredAmount the amount the insured person will obtain when the minDelay passes
     * @return
     */
    private RiskCoverage buildRiskCoverage(Flight flight, Integer minDelay, String coverageName, int insuredAmount) {
        double premiumAmount = calculatePremiumAmount(flight, minDelay, insuredAmount);
        return RiskCoverage.builder()
                .name(coverageName)
                .insuredAmount(insuredAmount)
                .premiumAmount(premiumAmount)
                .available(
                        flight.getFlightStatus() == null &&
                                flight.getExpectedArrivalDate().isAfter(LocalDateTime.now()) &&
                                premiumAmount < insuredAmount)
                .build();
    }

    private double calculatePremiumAmount(Flight flight, Integer minDelay, int insuredAmount) {
        double delayProbability = delayEstimator.computeProbabilityOfBeingDelayed(flight, minDelay);
        return Math.round(insuredAmount * (delayProbability * K + M1) + M2 + adaptiveM2.getOrDefault(flight.getFlightIdentifier(), 0d));
    }

    public void updateParameters(FlightIdentifier flightIdentifier, int insuredContractCount) {
        adaptiveM2.put(flightIdentifier, 10d * insuredContractCount);
    }

}

package pl.longhorn.tileset.extractor.prediction;

import org.springframework.stereotype.Component;

@Component
public class PredictionTimeService {

    private int deltaSum = 0;
    private int deltaAmount = 0;
    private int currentPrediction;

    public void addDelta(long deltaMilis) {
        deltaSum += deltaMilis;
        deltaAmount++;
        currentPrediction = deltaSum / deltaAmount;
    }

    public int getPrediction() {
        if (currentPrediction == 0) {
            return 900_000;
        } else {
            return currentPrediction;
        }
    }
}

import io.jenetics.*;
import io.jenetics.engine.Codecs;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.engine.Limits;
import io.jenetics.util.Factory;
import io.jenetics.util.Seq;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {

        int N = 8;

        final Engine<EnumGene<Integer>, Integer> engine = Engine
                .builder(
                        Main::fitness,
                        Codecs.ofPermutation(N)
                )
                .optimize(Optimize.MINIMUM)
                .survivorsSelector(new TournamentSelector<>(5))
                .offspringSelector(new LinearRankSelector<>())
                .populationSize(200)
                .alterers(
                        new SwapMutator<>(0.01),
                        new PartiallyMatchedCrossover<>(0.01)
                )
                .build();

        final Phenotype<EnumGene<Integer>, Integer> best = engine.stream()
                .limit(200)
//                .limit(Limits.byFitnessThreshold(N*(N-1)/2))
//                .limit(Limits.byExecutionTime(Duration.ofMinutes(30)))
                .collect(EvolutionResult.toBestPhenotype());

        int[] result = best.genotype().chromosome().stream().mapToInt(EnumGene::allele).toArray();

        System.out.println(best.fitness());

        printPositions(result, result.length);

    }

    public static Integer fitness(final int[] chromosome) {
        Integer fitness = 0;
        int fi;
        int fj;
        for (int i = 0; i < chromosome.length - 1; i++) {
            fi = chromosome[i];
            for (int j = i + 1; j < chromosome.length; j++) {
                fj = chromosome[j];
                if (fi == fj || Math.abs(i - j) == Math.abs(fi - fj))
                    fitness++;
            }
        }
        return fitness;
    }

    private static void printPositions(int[] board, int N) {
        for(int i = 0; i < N; ++i) {
            for(int j = 0; j < N; ++j) {
                if(board[i] == j) {
                    System.out.print("Q");
                } else {
                    System.out.print("X");
                }
            }
            System.out.print("\n");
        }
    }
}

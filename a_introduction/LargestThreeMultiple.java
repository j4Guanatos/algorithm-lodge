import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Created by nacho on 04/07/17.
 */
public class LargestThreeMultiple {

    public static String buildLargestThreeMultiple(int...digits) {
        NavigableMap<Integer, Long> incidence = Arrays.stream(digits)
                .boxed()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Function.identity(), Collectors.counting()),
                        TreeMap::new
                )).descendingMap();
        LongFunction<IntPredicate> filter = mod -> i -> mod != 0 && i % 3 == mod;

        long mod = incidence.entrySet().stream()
                .mapToLong(e -> e.getKey().intValue() * e.getValue().longValue())
                .sum()
                % 3;

        IntFunction<IntConsumer> replacer = i -> red -> incidence.replace(i, incidence.get(i) - red);

        Optional<Integer> removal = incidence.keySet().stream()
                .mapToInt(Integer::intValue)
                .filter(filter.apply(mod))
                .boxed()
                .sorted()
                .findFirst();

        if (removal.isPresent()) {
            replacer.apply(removal.get()).accept(1);
        } else {
            Map<Integer, Long> removals = incidence.entrySet().stream()
                    .filter(e -> filter.apply((3 - mod) % 3).test(e.getKey()))
                    .sorted()
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            int entriesToRemove = 2;

            for (Map.Entry<Integer, Long> entry : removals.entrySet()) {
                int remove = entry.getValue() >= 2 && entriesToRemove == 2 ? 2 : 1;
                replacer.apply(entry.getKey()).accept(remove);
                entriesToRemove -= remove;
                if (entriesToRemove == 0) break;
            }
        }

        String number = incidence.entrySet().stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> String.join("", Collections.nCopies(e.getValue().intValue(), e.getKey().toString())))
                .collect(Collectors.joining());

        return number.length() > 0 ? number : "0";
    }

    public static void main(String...args) {
        System.out.println(buildLargestThreeMultiple(9,9,8,1,1,4,1,8));
    }
}

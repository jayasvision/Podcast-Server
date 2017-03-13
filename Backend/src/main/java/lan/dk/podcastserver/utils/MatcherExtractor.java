package lan.dk.podcastserver.utils;

import javaslang.control.Option;
import javaslang.control.Try;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kevin on 04/03/2017
 */
public class MatcherExtractor {

    private final Matcher matcher;
    private final Boolean isFind;

    private MatcherExtractor(Matcher matcher) {
        this.matcher = matcher;
        this.isFind = matcher.find();
    }

    public static PatternExtractor from(Pattern pattern) {
        return new PatternExtractor(pattern);
    }

    public Option<String> group(Integer group) {
        if (!isFind) {
            return Option.none();
        }

        return Try.of(() -> matcher.group(group)).toOption();
    }

    public static class PatternExtractor {

        private final Pattern pattern;

        private PatternExtractor(Pattern pattern) { this.pattern = pattern; }

        public MatcherExtractor on(String value) { return new MatcherExtractor(pattern.matcher(value)); }

    }

}
package bot.main.api.revai;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MessageTranscript {

    private List<Monologue> monologues;

    @Getter
    @NoArgsConstructor
    public class Monologue {

        private List<Element> elements;

        @Getter
        @NoArgsConstructor
        public class Element {

            private String value;
        }
    }
}

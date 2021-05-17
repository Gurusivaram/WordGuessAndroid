package retrofit.model;

public class RetroWord {
    public String correctAnswer;
    public String notRelatedWord;
    public String relatedWord;
    public String word;

    public RetroWord(String notRelatedWord, String relatedWord, String word) {
        this.correctAnswer = relatedWord;
        this.notRelatedWord = notRelatedWord;
        this.relatedWord = relatedWord;
        this.word = word;
    }
}

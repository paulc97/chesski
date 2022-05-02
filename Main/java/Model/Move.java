package Model;

/**
 * Represents a generated move that can be evaluated and carried out by handing the 'getMoveString' over to the controller
 */
public class Move {
    String sourceField;
    String targetField;
    int rating;

    //TODO: add all relevant fields that will be required to assess the move (involved pieces, captured pieces etc.)

    public String getMoveString(){
        return this.sourceField + this.targetField;
    }

    //TODO: Method to get the fen String of this move and create a new Game class with all its bitboards?
    // Or discuss other methods/places to generate the following board...

}

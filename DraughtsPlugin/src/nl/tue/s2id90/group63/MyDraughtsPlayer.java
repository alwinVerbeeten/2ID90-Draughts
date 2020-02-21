package nl.tue.s2id90.group63;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import java.util.Collections;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Implementation of the DraughtsPlayer interface.
 * @author huub
 */
// ToDo: rename this class (and hence this file) to have a distinct name
//       for your player during the tournament
public class MyDraughtsPlayer  extends DraughtsPlayer{
    private int bestValue=0;
    int maxSearchDepth = 10;
    int kingWorth = 15;
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public MyDraughtsPlayer(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = 10;
    }
    
    @Override public Move getMove(DraughtsState s) {
        DraughtsState s2 = s.clone();
        Move bestMove = null;
        bestValue = 0;
        DraughtsNode node = new DraughtsNode(s);    // the root of the search tree
        boolean done = false;
        int depth = 1;
        while(!done){
            try {
                // compute bestMove and bestValue in a call to alphabeta
                bestValue = alphaBeta(node, MIN_VALUE, MAX_VALUE, depth);

                // store the bestMove found uptill now
                // NB this is not done in case of an AIStoppedException in alphaBeat()
                bestMove  = node.getBestMove();

                // print the results for debugging reasons
                System.err.format(
                    "%s: depth= %2d, best move = %5s, value=%d\n", 
                    this.getClass().getSimpleName(), depth, bestMove, bestValue
                );
                depth++; 
                if(depth > 2000){
                    done = true;
                }
        } catch (AIStoppedException ex) {  
            done = true;
        }}
        
        if (bestMove==null) {
            System.err.println("no valid move found!");
            System.out.print(s);
            System.out.print(s2);
            return getRandomValidMove(s2);
        } else {
            return bestMove;
        }
    } 

    /** This method's return value is displayed in the AICompetition GUI.
     * 
     * @return the value for the draughts state s as it is computed in a call to getMove(s). 
     */
    @Override public Integer getValue() { 
       return bestValue;
    }

    /** Tries to make alphabeta search stop. Search should be implemented such that it
     * throws an AIStoppedException when boolean stopped is set to true;
    **/
    @Override public void stop() {
       stopped = true; 
    }
    
    /** returns random valid move in state s, or null if no moves exist. */
    Move getRandomValidMove(DraughtsState s) {
        List<Move> moves = s.getMoves();
        Collections.shuffle(moves);
        return moves.isEmpty()? null : moves.get(0);
    }
    
    /** Implementation of alphabeta that automatically chooses the white player
     *  as maximizing player and the black player as minimizing player.
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth maximum recursion Depth
     * @return the computed value of this node
     * @throws AIStoppedException
     **/
    int alphaBeta(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException
    {
        if (node.getState().isWhiteToMove()) {
            return alphaBetaMax(node, alpha, beta, depth);
        } else  {            
            return alphaBetaMin(node, alpha, beta, depth);
        }
    }
    
    /** Does an alphabeta computation with the given alpha and beta
     * where the player that is to move in node is the minimizing player.
     * 
     * <p>Typical pieces of code used in this method are:
     *     <ul> <li><code>DraughtsState state = node.getState()</code>.</li>
     *          <li><code> state.doMove(move); .... ; state.undoMove(move);</code></li>
     *          <li><code>node.setBestMove(bestMove);</code></li>
     *          <li><code>if(stopped) { stopped=false; throw new AIStoppedException(); }</code></li>
     *     </ul>
     * </p>
     * @param node contains DraughtsState and has field to which the best move can be assigned.
     * @param alpha
     * @param beta
     * @param depth  maximum recursion Depth
     * @return the compute value of this node
     * @throws AIStoppedException thrown whenever the boolean stopped has been set to true.
     */
     int alphaBetaMin(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; 
        System.out.print("STOPPED!");
        throw new AIStoppedException(); 
        }
        DraughtsState state = node.getState();
        //  if DepthLimitReached(Node) Return(Rating(Node))
        if (depth <= 0 || state.isEndState()) { 
           return evaluate(state);
        }
        // NewNodes = Successors(Node)
        List<Move> moves = state.getMoves();
        // Make sure moves isn't empty
        if(moves == null || moves.size() < 1){
            System.out.print("ERROR!");
            return beta;
        }
        Move bestMove = moves.get(0); // TO DO --------------------
        //While NewNodes != ∅
        for(Move move : moves){
           // Move to next boardstate
           state.doMove(move);
           int oldBeta = beta;
           // β = Minumum(β, AlphaBetaMax(First(NewNodes),α,β))
           beta = Math.min(beta, alphaBetaMax(new DraughtsNode(state), alpha, beta, depth - 1));
           
           // Move to previous boardstate
           state.undoMove(move);
           
           // Store the the new move if it is beter
           if(beta < oldBeta){
               bestMove = move;
           }
           
           //if α≥β Return(α)
           if(alpha >= beta){ 
               node.setBestMove(bestMove);
               return alpha;
           }
        }
        // Return(β)
        node.setBestMove(bestMove);
        return beta;
     } 
    
    int alphaBetaMax(DraughtsNode node, int alpha, int beta, int depth)
            throws AIStoppedException {
        if (stopped) { stopped = false; 
        System.out.print("STOPPED!");
        throw new AIStoppedException(); 
        }
        DraughtsState state = node.getState();
        //  if DepthLimitReached(Node) Return(Rating(Node))
        if (depth <= 0 || state.isEndState()) { 
           return evaluate(state);
        }
        // NewNodes = Successors(Node)
        List<Move> moves = state.getMoves();
        // Make sure moves isn't empty
        if(moves == null || moves.size() < 1){
            System.out.print("ERROR!");
            return alpha;
        }
        Move bestMove = moves.get(0); // TO DO --------------------
        //While NewNodes != ∅
        for(Move move : moves){
           // Move to next boardstate
           state.doMove(move);
           int oldAlpha = alpha;
           // α = Maximim(α, AlphaBetaMin(First(NewNodes),α,β))
           alpha = Math.max(alpha, alphaBetaMin(new DraughtsNode(state), alpha, beta, depth - 1));
           
           // Move to previous boardstate
           state.undoMove(move);
           
           // Store the the new move if it is beter
           if(alpha < oldAlpha){
               bestMove = move;
           }
           
           //if α≥β Return(β)
           if(alpha >= beta){ 
               node.setBestMove(bestMove);
               return beta;
           }
        }
        // Return(α)
        node.setBestMove(bestMove);
        return alpha;
    }

    /** A method that evaluates the given state. */
    // ToDo: write an appropriate evaluation function
    int evaluate(DraughtsState state) { 
        //obtain pieces array
        int[] pieces = state.getPieces();
        
        int computedValue = 0;
        
        //Check each square on the board for a piece, add points for white, remove points for black.
        for (int piece : pieces) {
            switch (piece) {
                case 0: // empty spot
                    break;
                case DraughtsState.WHITEPIECE: // piece is a white piece
                    computedValue = computedValue + 10;
                    break;
                case DraughtsState.BLACKPIECE: // piece is a black piece
                    computedValue = computedValue - 10;
                    break;
                case DraughtsState.WHITEKING: // piece is a white king
                    computedValue = computedValue + kingWorth;
                    break;
                case DraughtsState.BLACKKING: // piece is a black king
                    computedValue = computedValue - kingWorth;
                    break;
            }
        }
        return computedValue;
    }
}

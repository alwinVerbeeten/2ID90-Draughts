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
    final int PIECESVALUE = 10; // Worth of a piece
    final int KINGSWORTH = 20; // Worth of a king piece
    final int LEADVALUE = 5; // Extra worth of pieces if youre winning / losing
    final int DISTANCEVALUE = 1; // Penalty if piece is to far from other pieces
    final int CLUTTERINGVALUE = 1; // Pieces are worth more if they are near others
    final int CHARGEVALUE = 2; // Pieces are worth more if they are further on the board
    final int EDGEVALUE = 2;
    final int GOLDLIMIT = 10;
    final int GOLDVALUE = 2; 
    
    int[][] groups = new int[51][4];
    
    /** boolean that indicates that the GUI asked the player to stop thinking. */
    private boolean stopped;

    public MyDraughtsPlayer(int maxSearchDepth) {
        super("best.png"); // ToDo: replace with your own icon
        this.maxSearchDepth = 10;
        fillAdjectends();
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
            System.out.println("you are white");
            return alphaBetaMax(node, alpha, beta, depth);
        } else  { 
            System.out.println("you are black");
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
           if(alpha > oldAlpha){
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
        int blackPieces = 0;
        int whitePieces = 0;
        //obtain pieces array
        int[] pieces = state.getPieces();
        boolean white = true;
        int computedValue = 0;
        int value = 0;
        //Check each square on the board for a piece, add points for white, remove points for black.
        for (int i = 1; i < pieces.length; i++) {
            //System.out.println("i: " + i);
            int piece = pieces[i];
            value = 0;
            switch (piece) {
                case 0: // empty spot
                    continue;
                case DraughtsState.WHITEPIECE: // piece is a white piece
                    value = PIECESVALUE;
                    white = true;
                    whitePieces++;
                    break;
                case DraughtsState.BLACKPIECE: // piece is a black piece
                    value = -1 * PIECESVALUE;
                    white = false;
                    blackPieces++;
                    break;
                case DraughtsState.WHITEKING: // piece is a white king
                    value = KINGSWORTH;
                    white = true;
                    whitePieces++;
                    break;
                case DraughtsState.BLACKKING: // piece is a black king
                    value = -1 * KINGSWORTH;
                    white = false;
                    blackPieces++;
                    break;
            }
            //System.out.println("value1: " + value);
            //System.out.println("white: " + white);
            // If piece is on an edge it is worth less
            value = value + checkEdge(i, white);
            
            // Check whether the piece is surrounded by pieces of its color
            // And give them bonus points if they do
            value = value + checkClutter(i, pieces, white);
            
            // If a piece is deeper on the board, give it more value
            value = value + checkDepth(i, white);
            
            // Check for the Golden Stone
            if(i == 48 && white){
                value = value + checkGold(blackPieces);
            }
            
            if(i == 3 && !white){
                value = value - checkGold(whitePieces);
            }
            
            //System.out.println("value2: " + value);
            computedValue = computedValue + value;
        }
        
        // TRADE PIECES WHEN YOU ARE AHEAD!
        computedValue = computedValue + getLead(whitePieces - blackPieces, state.isWhiteToMove());
        
        //System.out.println("Computed value: " + computedValue);
        return computedValue;
    }
    
    // If piece is on an edge it is worth less
    public int checkEdge(int i, boolean white){
        if(i % 10 == 5 || i % 10 == 6){
            if(white){
                return -1 * EDGEVALUE;
            }else{
                return EDGEVALUE;
            }
        }
        return 0;
    }
    
    // Check how many pieces surround this piece
    public int checkClutter(int i, int[] pieces, boolean white){
        int value = 0;
        for(int piece: groups[i]){
            if(piece > 0){
                if(white){
                    if(pieces[piece] == DraughtsState.WHITEPIECE || 
                            pieces[piece] == DraughtsState.WHITEKING){
                        value = value + CLUTTERINGVALUE;
                    }
                }else{
                    if(pieces[piece] == DraughtsState.BLACKPIECE || 
                            pieces[piece] == DraughtsState.BLACKKING){
                        value = value - CLUTTERINGVALUE;
                    }
                }
            }
        }
        
        return value;
    }
    
    // If a piece is deeper on the board, give it more value
    public int checkDepth(int i, boolean white){
        int value = 0;
        if(white){
            if(i < 16){
                value = value + CHARGEVALUE; 
                if(i < 11){
                    value = value + CHARGEVALUE; 
                    if(i < 6){
                        value = value + CHARGEVALUE; 
                    }
                }
            }
        }else{
            if(i > 35){
                value = value - CHARGEVALUE; 
                if(i > 40){
                    value = value - CHARGEVALUE; 
                    if(i > 45){
                        value = value - CHARGEVALUE; 
                    }
                }
            }
        }
            
        return value;
    }
    
    // Check whether a piece is a Golden Stone
    public int checkGold(int pieces){
        if(pieces > GOLDLIMIT){
            return (pieces - GOLDLIMIT) * GOLDVALUE;
        }
        return 0;
    }
    
    // TRADE PIECES WHEN YOU ARE AHEAD!
    public int getLead(int lead, boolean white){
        // If white is winning and you are white
        if(lead > 1 && white){
            // Then get extra value since you are winning
            return LEADVALUE;
        }

        // If black is winning and you are black
        if(lead < 1 && !white){
            // Then get extra value since you are winning
            return -1 * LEADVALUE;
        }
        return 0;
    }

    // Create a table where the adjectend spots are saved for each spot
    // on the field
    private void fillAdjectends() {
        for(int i = 1; i <= 50; i++){
            boolean below = true;
            boolean right = true;
            boolean left = true;
            boolean above = true;
            boolean firstRows = false;
            int value = 0;

            // if top layer
            if(i <= 5){
                above = false;
            }
            if(i >= 46){
                below = false;
            }
            if(i % 10 == 5){
                right = false;
            }
            if(i % 10 == 6){
                left = false;
            }
            if(i % 10 <= 5 && i % 10 > 0){
                firstRows = true;
            }
            int index = -5;
            if(!firstRows){
               index --;
            }
            if(above && left){
                groups[i][0] = i + index;
            }else{
                groups[i][0] = 0;
            }

            if(above && right){
                groups[i][1] = i + index + 1;
            }else{
                groups[i][1] = 0;
            }

            if(below && left){
                groups[i][2] = i + index + 10;
            }else{
                groups[i][2] = 0;
            }

            if(below && right){
                groups[i][3] = i + index + 11;
            }else{
                groups[i][3] = 0;
            }
        }
    }
}

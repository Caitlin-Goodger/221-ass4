package swen221.cards.util;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import swen221.cards.core.Card;
import swen221.cards.core.Card.Suit;
import swen221.cards.core.Hand;
import swen221.cards.core.Player;
import swen221.cards.core.Trick;

/**
 * Implements a simple computer player who plays the highest card available when
 * the trick can still be won, otherwise discards the lowest card available. In
 * the special case that the player must win the trick (i.e. this is the last
 * card in the trick), then the player conservatively plays the least card
 * needed to win.
 * 
 * @author David J. Pearce
 * 
 */
public class SimpleComputerPlayer extends AbstractComputerPlayer {

	public SimpleComputerPlayer(Player player) {
		super(player);
	}

	@Override
	public Card getNextCard(Trick trick) {		
		Suit trumps = trick.getTrumps();
		List<Card> played = trick.getCardsPlayed();
		//If this is the first card to be played in thr trick
		if(played.size() ==0) {
			if(trumps == null) {//If trumps are null then play the best card
				PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
					if(c1.rank().ordinal()<c2.rank().ordinal()) {return 1;}
					else if(c1.rank().ordinal()>c2.rank().ordinal()) {return -1;}
					else if(c1.suit().ordinal()<c2.suit().ordinal()) {return 1;}
					else if(c1.suit().ordinal()>c2.suit().ordinal()) {return -1;}
					else {return 0;}
					});
					for(Card c : player.hand) {
						q.add(c);
					}
					return q.poll(); //Play that best card
			}
			Set<Card> matches = player.hand.matches(trumps); //Get the cards in the hand that are trumps
			if(matches.size()>0) { //If have trump cards
				PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
					if(c1.suit().ordinal()<c2.suit().ordinal()) {return 1;}
					else if(c1.suit().ordinal()>c2.suit().ordinal()) {return -1;}
					else if(c1.rank().ordinal()<c2.rank().ordinal()) {return 1;}
					else if(c1.rank().ordinal()>c2.rank().ordinal()) {return -1;}
					else {return 0;}
					});
					for(Card c : matches) {
						q.add(c);
					}
					return q.poll(); //Play the best one
			} else if(matches.size()==0) { //If no trumps
				PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
				if(c1.rank().ordinal()<c2.rank().ordinal()) {return 1;}
				else if(c1.rank().ordinal()>c2.rank().ordinal()) {return -1;}
				else if(c1.suit().ordinal()<c2.suit().ordinal()) {return 1;}
				else if(c1.suit().ordinal()>c2.suit().ordinal()) {return -1;}
				else {return 0;}
				});
				for(Card c : player.hand) {
					q.add(c);
				}
				return q.poll(); //Play the best card
			}
		}
		//If people have already played
		Set<Card> matches = player.hand.matches(played.get(0).suit());
		if(matches.size()>0) { //If have to follow suit
			PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
				if(c1.rank().ordinal()<c2.rank().ordinal()) {return 1;}
				else if(c1.rank().ordinal()>c2.rank().ordinal()) {return -1;}
				else if(c1.suit().ordinal()>c2.suit().ordinal()) {return 1;}
				else if(c1.suit().ordinal()<c2.suit().ordinal()) {return -1;}
				else {return 0;}
				});
				for(Card c : matches) {
					q.add(c);
				}
				Card play = q.poll(); //Get the best card of the suit
				boolean win = true;
				for(Card c : played) { //See if that card wins
					if(play.compareTo(c) == -1) {
						win = false;
					}
				}
				if(!win) { //If it doesn't reverse the Priority Queue
					PriorityQueue<Card> qR = new PriorityQueue<Card>((Card c1, Card c2)->{
						if(c1.rank().ordinal()<c2.rank().ordinal()) {return -1;}
						else if(c1.rank().ordinal()>c2.rank().ordinal()) {return 1;}
						else if(c1.suit().ordinal()>c2.suit().ordinal()) {return 1;}
						else if(c1.suit().ordinal()<c2.suit().ordinal()) {return -1;}
						else {return 0;}
						});
						for(Card c : matches) {
							qR.add(c);
						}
						return qR.poll(); //Discard the lowest card
				}
				Card last = play;
				boolean stillWin = true;
				//If last to play, then play the lowest card that will win
				while(stillWin && !q.isEmpty() && played.size() == 3) {
					Card next = q.poll();
					for(Card c : played) {
						if(play.compareTo(c) == -1) { //Keep going down in cards until finding one that doesn't win
							stillWin = false;
						}
					}
					last = next; 
				}
				return last;
		}
		if(trumps != null) { //If there is trumps
			Set<Card> matchesT = player.hand.matches(trumps); //Get the cards from the hand on trumps
			if(matchesT.size()>0) {
				PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
					if(c1.suit().ordinal()<c2.suit().ordinal()) {return -1;}
					else if(c1.suit().ordinal()>c2.suit().ordinal()) {return 1;}
					else if(c1.rank().ordinal()<c2.rank().ordinal()) {return 1;}
					else if(c1.rank().ordinal()>c2.rank().ordinal()) {return -1;}
					else {return 0;}
					});
					for(Card c : matchesT) {
						q.add(c);
					}
					Card play = q.poll();
					boolean win = true;
					for(Card c : played) { //Get the best card and check that it wins
						if(play.suit().ordinal() == c.suit().ordinal() && play.rank().ordinal()<c.rank().ordinal()) {
							win = false;
						}
					}
					if(!win) { //If it doesn't then just discard a card
						PriorityQueue<Card> qR = new PriorityQueue<Card>((Card c1, Card c2)->{
							if(c1.rank().ordinal()<c2.rank().ordinal()) {return -1;}
							else if(c1.rank().ordinal()>c2.rank().ordinal()) {return 1;}
							else if(c1.suit().ordinal()<c2.suit().ordinal()) {return -1;}
							else if(c1.suit().ordinal()>c2.suit().ordinal()) {return 1;}
							else {return 0;}
							});
							for(Card c : player.hand) {
								qR.add(c);
							}
							return qR.poll();
					}
					Card last = play;
					boolean stillWin = true;
					//If last to play, then play the lowest card that will win
					while(stillWin && !q.isEmpty() && played.size() == 3) {
						Card next = q.poll();
						for(Card c : played) {
							if(play.compareTo(c) == -1) {
								stillWin = false;
							}
						}
						last = next;
					}
					return last;
		}
		}
		//Else just play the best card
		PriorityQueue<Card> q = new PriorityQueue<Card>((Card c1, Card c2)->{
			if(c1.rank().ordinal()<c2.rank().ordinal()) {return -1;}
			else if(c1.rank().ordinal()>c2.rank().ordinal()) {return 1;}
			else if(c1.suit().ordinal()>c2.suit().ordinal()) {return 1;}
			else if(c1.suit().ordinal()<c2.suit().ordinal()) {return -1;}
			else {return 0;}
			});
			for(Card c : player.hand) {
				q.add(c);
			}
			
			return q.poll();
	}	
	
}

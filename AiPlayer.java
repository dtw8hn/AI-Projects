//get the one at the end before the middle game and the end case


package ttr.model.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Comparator;

import com.sun.prism.paint.Color;

import ttr.model.destinationCards.Destination;
import ttr.model.destinationCards.Route;
import ttr.model.trainCards.TrainCardColor;
import edu.virginia.engine.events.general.EventDispatcher;
import ttr.model.destinationCards.DestinationTicket;
import ttr.model.destinationCards.Routes;
import ttr.model.events.PlayerMakeMoveEvent;
import ttr.model.events.PlayerStatChangeEvent;
import ttr.model.trainCards.TrainCard;
import ttr.view.scenes.TTRGamePlayScene;

class MyComparator implements Comparator<Route> {

	public int compare(Route a, Route b) {

		return b.getCost() - a.getCost();
		// TODO
	}
}

class smallComparator implements Comparator<Route> {

	public int compare(Route a, Route b) {

		return a.getCost() - b.getCost();
		// TODO
	}
}



public class AiPlayer extends Player {
	int turns = 0;
	int pieces;
	Comparator<Route> com = new MyComparator();
	Comparator<Route> end = new smallComparator();
	ArrayList<Route> path = new ArrayList<Route>();
	ArrayList<Route> endpaths = new ArrayList<Route>();

	public AiPlayer(String name) {
		// TODO Auto-generated constructor stub
		super(name);
	}

	@Override
	public void makeMove() {
		// TODO Auto-generated method stub
		turns++;
		pieces = this.getNumTrainPieces();
		// White, Black, Yellow, Blue, Green, Orange, Purple/Pink & finally Red
		// eventually
		int numofBlue = this.getNumTrainCardsByColor(TrainCardColor.blue);
		int numofBlack = this.getNumTrainCardsByColor(TrainCardColor.black);
		int numofGreen = this.getNumTrainCardsByColor(TrainCardColor.green);
		int numofOrange = this.getNumTrainCardsByColor(TrainCardColor.orange);
		int numofPurple = this.getNumTrainCardsByColor(TrainCardColor.purple);
		int numofRed = this.getNumTrainCardsByColor(TrainCardColor.red);
		int numofYellow = this.getNumTrainCardsByColor(TrainCardColor.yellow);
		int numofWhite = this.getNumTrainCardsByColor(TrainCardColor.white);
		int numofRainBow = this.getNumTrainCardsByColor(TrainCardColor.rainbow);

		ArrayList<Route> Choke = new ArrayList<Route>();
		ArrayList<Route> All = Routes.getInstance().getAllRoutes();
		Choke.add(new Route(Destination.LosAngeles, Destination.Phoenix, 3,
				TrainCardColor.rainbow));
		Choke.add(new Route(Destination.Houston, Destination.NewOrleans, 2,
				TrainCardColor.rainbow));
		Choke.add(new Route(Destination.Nashville, Destination.Atlanta, 1,
				TrainCardColor.rainbow));
		Choke.add(new Route(Destination.Seattle, Destination.Portland, 1,
				TrainCardColor.rainbow));
		Choke.add(new Route(Destination.Dallas, Destination.Houston, 1,
				TrainCardColor.rainbow));

		boolean choke = false;
		// Beginning game
		if (turns < 15) {

			// can I take a choke point?
			// check choke points
			for (int j = 0; j < Choke.size(); j++) {
				Route p = Routes
						.getInstance()
						.getRoutes(Choke.get(j).getDest1(),
								Choke.get(j).getDest2()).get(0);
				// You might have to break this up... One line up
				if (p.getOwner() == null) {
					// might have to use == null
					int cost = p.getCost();
					// White, Black, Yellow, Blue, Green, Orange, Purple/Pink &
					// finally Red eventually
					if (numofRed + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.red);
						choke = true;
					}
					if (numofPurple + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.purple);
						choke = true;
					}
					if (numofOrange + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.orange);
						choke = true;
					}
					if (numofGreen + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.green);
						choke = true;
					}
					if (numofBlue + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.blue);
						choke = true;
					}
					if (numofYellow + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.yellow);
						choke = true;
					}
					if (numofBlack + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.black);
						choke = true;
					}
					if (numofWhite + numofRainBow >= cost) {
						super.claimRoute(p, TrainCardColor.white);
						choke = true;
					}

					// try and buy the choke point
				}
			}
			// if a rainbow is present, grab it.

			if (choke == false) {
				for (int i = 0; i < this.getFaceUpCards().size(); i++) {
					if (this.getFaceUpCards().get(i).getColor()
							.equals(TrainCardColor.rainbow)) {
						super.drawTrainCard(i + 1);
						choke = true;
					}
				}
			}
			if (choke == false)
				super.drawTrainCard(0);
				choke=true;
		} 
		else {
			// if I don't have a destination, grab a destination.
			// endgame
			int total = 0;
			for (int i = 0; i < All.size(); i++) {
				Player tempp = All.get(i).getOwner();
				if (tempp != this) {
					if (tempp != null) {
						total += All.get(i).getCost();
					}
				}
			}
			if ((total > 35 || pieces <= 17)) {
				
				// ADD A CHECK FOR IF THIS PATH IS EVEN FEASIBLE
				if(this.getDestinationTickets().size()>1){
				int min = 90;
				int keep = 0;
				for (int y = 0; y < this.getDestinationTickets().size(); y++) {
					if (this.getDestinationTickets().get(y).getValue() < min) {
						min = this.getDestinationTickets().get(y).getValue();
						keep = y;
					}
				}
				ArrayList<Destination> endpath = getPath(this
						.getDestinationTickets().get(keep).getFrom(), this
						.getDestinationTickets().get(keep).getTo());
				
				endPath(endpath);
				int max = 0;
				int keep2 = 0;
				for (int y = 0; y < this.getDestinationTickets().size(); y++) {
					if (this.getDestinationTickets().get(y).getValue() > max) {
						max = this.getDestinationTickets().get(y).getValue();
						keep2 = y;
					}
				}
				// ArrayList<Destination> mypath;
				ArrayList<Destination> mypath = getPath(this
						.getDestinationTickets().get(keep2).getFrom(), this
						.getDestinationTickets().get(keep2).getTo());
				// ADD A CHECK FOR IF THIS PATH IS EVEN FEASIBLE
				Path(mypath);
				int big=path.size();//big list
				int bigcount=0;
				int small=endpaths.size();//small list
				int smallcount=0;
				for(int x=0;x<endpaths.size();x++){
					smallcount+= endpaths.get(x).getCost();
					if (endpaths.get(x).getOwner()==this){
						small--;
					}
				
				for(int y=0;y<mypath.size();y++){
					bigcount+=path.get(y).getCost();
					if (endpaths.get(x).getOwner()==this){
						if(path.get(y).getOwner()==this){
							big--;
						}
					}
				}
				ArrayList<Route> decided = new ArrayList<Route>();
				if(big<=small&&pieces>=bigcount){
					decided=path;
				}
				else{
					decided=endpaths;
				}
				//start changing to decided
				for (int i = 0; i < decided.size(); i++) {
					if((decided.get(i).getColor().equals(TrainCardColor.rainbow))&&decided.get(i).getOwner()==null){
						 if(numofBlue+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.blue);
								choke=true;
						 }
						 if(numofBlack+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.black);
								choke=true;

						 }
						 if(numofGreen+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.green);
								choke=true;

						 }
						 if(numofOrange+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.orange);
								choke=true;

						 }if(numofPurple+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.purple);
								choke=true;

						 }
						 if(numofRed+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.red);
								choke=true;

						 }
						 if(numofYellow+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.yellow);
								choke=true;

						 }
						 if(numofWhite+numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.white);
								choke=true;
						 }
						 if(numofRainBow>=decided.get(i).getCost()&&pieces>=decided.get(i).getCost()){
								this.claimRoute(decided.get(i), TrainCardColor.rainbow);
								choke=true;
						 }
						//use my colors
							//if()
					
					}
					int mycolor = this.getNumTrainCardsByColor(decided.get(i).getColor());
					// ADD A CHECK FOR THE GREY ROUTES
					// else{
					if (mycolor + numofRainBow >= decided.get(i).getCost()) {
						if(decided.get(i).getOwner()==null&&choke==false&&pieces>=decided.get(i).getCost()){
						this.claimRoute(decided.get(i), decided.get(i).getColor());
						choke=true;
					}
					}
				}
				
				//look at the face up cards to see if theres a rainbow
				for (int i = 0; i < this.getFaceUpCards().size(); i++) {
					if (this.getFaceUpCards().get(i).getColor().equals(TrainCardColor.rainbow)&&choke==false) {
						super.drawTrainCard(i + 1);
						choke = true;
					}
				}
				
				//look to see if I'm two away from buying a route on my path
				for (int i = 0; i < decided.size(); i++) {
					
					int mycolor = this.getNumTrainCardsByColor(decided.get(i).getColor());
				if (mycolor + 2 + numofRainBow >= decided.get(i).getCost()) {
					for (int j = 0; j < this.getFaceUpCards().size(); j++) {
						if (this.getFaceUpCards().get(j).getColor().equals(decided.get(i).getColor())&&choke==false) {
							super.drawTrainCard(j + 1);
							choke=true;
						}
					}
				}
				}
				

				super.drawTrainCard(0);
							
				
				
				}
				}
				else if(this.getDestinationTickets().size()==1){
				// do it with the one	
					ArrayList<Destination> mypath = getPath(this.getDestinationTickets().get(0).getFrom(), this.getDestinationTickets().get(0).getTo());
					// ADD A CHECK FOR IF THIS PATH IS EVEN FEASIBLE
					Path(mypath);
					//Can I buy a route on the path of my destination ticket
					for (int i = 0; i < path.size(); i++) {
						if((path.get(i).getColor().equals(TrainCardColor.rainbow))&&path.get(i).getOwner()==null){
							 if(numofBlue+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.blue);
									choke=true;
							 }
							 if(numofBlack+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.black);
									choke=true;

							 }
							 if(numofGreen+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.green);
									choke=true;

							 }
							 if(numofOrange+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.orange);
									choke=true;

							 }if(numofPurple+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.purple);
									choke=true;

							 }
							 if(numofRed+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.red);
									choke=true;

							 }
							 if(numofYellow+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.yellow);
									choke=true;

							 }
							 if(numofWhite+numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.white);
									choke=true;
							 }
							 if(numofRainBow>=path.get(i).getCost()&&pieces>=path.get(i).getCost()){
									this.claimRoute(path.get(i), TrainCardColor.rainbow);
									choke=true;
							 }
							//use my colors
								//if()
						
						}	
						int mycolor = this.getNumTrainCardsByColor(path.get(i).getColor());
						// ADD A CHECK FOR THE GREY ROUTES
						// else{
						if (mycolor + numofRainBow >= path.get(i).getCost()) {
							if(path.get(i).getOwner()==null&&pieces>=path.get(i).getCost()){
							this.claimRoute(path.get(i), path.get(i).getColor());
							choke=true;
						}
						}
					}
					
					//look at the face up cards to see if theres a rainbow
					for (int i = 0; i < this.getFaceUpCards().size(); i++) {
						if (this.getFaceUpCards().get(i).getColor()
								.equals(TrainCardColor.rainbow)) {
							super.drawTrainCard(i + 1);
							choke = true;
						}
					}
					
					//look to see if I'm two away from buying a route on my path
					for (int i = 0; i < path.size(); i++) {
						
						int mycolor = this.getNumTrainCardsByColor(path.get(i).getColor());
					if (mycolor + 2 + numofRainBow >= path.get(i).getCost()) {
						for (int j = 0; j < this.getFaceUpCards().size(); j++) {
							if (this.getFaceUpCards().get(j).getColor()
									.equals(path.get(i).getColor())) {
								super.drawTrainCard(j + 1);
								choke=true;
							}
						}
					}
					}
					

					super.drawTrainCard(0);
					}
					//No destination tickets
					Collections.sort(All,com);
					for (int p = 0; p < All.size(); p++) {
						int mycolor = this.getNumTrainCardsByColor(All.get(p).getColor());
						if (mycolor + numofRainBow >= All.get(p).getCost()) {
							if(All.get(p).getOwner()==null && pieces>=All.get(p).getCost()&&choke==false){
							super.claimRoute(All.get(p), All.get(p).getColor());
							choke=true;
							// get that mother fucking route;
						}
						else if(mycolor+numofRainBow+1>=All.get(p).getCost()){
							for(int q=0;q<this.getFaceUpCards().size();q++){
								if(this.getFaceUpCards().get(q).getColor().equals(mycolor)&&choke==false){
									super.drawTrainCard(q+1);
									choke=true;
								}
							}
						}
							
						}
					}
					super.drawTrainCard(0);	
				
				} 
			//else {

				// Middle Game
				
				//draw a destination ticket if I don't have any more
				if (this.getDestinationTickets().size() == 0) {
					super.drawDestinationTickets();
				}
				
				else{
				int max = 0;
				int keep = 0;
				//find the destination ticket with the highest cost
				for (int y = 0; y < this.getDestinationTickets().size(); y++) {
					if (this.getDestinationTickets().get(y).getValue() > max) {
						max = this.getDestinationTickets().get(y).getValue();
						keep = y;
					}
				}
				// ArrayList<Destination> mypath;
				ArrayList<Destination> mypath = getPath(this.getDestinationTickets().get(keep).getFrom(), this.getDestinationTickets().get(keep).getTo());
				Path(mypath);
				//takes all destinations in path
				//Can I buy a route on the path of my destination ticket
				for (int i = 0; i < path.size(); i++) {
					if((path.get(i).getColor().equals(TrainCardColor.rainbow))&&path.get(i).getOwner()==null){
						 if(numofBlue+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.blue);
								choke=true;
						 }
						 if(numofBlack+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.black);
								choke=true;

						 }
						 if(numofGreen+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.green);
								choke=true;

						 }
						 if(numofOrange+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.orange);
								choke=true;

						 }if(numofPurple+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.purple);
								choke=true;

						 }
						 if(numofRed+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.red);
								choke=true;

						 }
						 if(numofYellow+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.yellow);
								choke=true;

						 }
						 if(numofWhite+numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.white);
								choke=true;
						 }
						 if(numofRainBow>=path.get(i).getCost()){
								this.claimRoute(path.get(i), TrainCardColor.rainbow);
								choke=true;
						 }
						//use my colors
							//if()
					
					}
					//start of else
					int mycolor = this.getNumTrainCardsByColor(path.get(i).getColor());
					// ADD A CHECK FOR THE GREY ROUTES
					// else{
					if (mycolor + numofRainBow >= path.get(i).getCost()) {
						if(path.get(i).getOwner()==null&&choke==false){
						this.claimRoute(path.get(i), path.get(i).getColor());
						choke=true;
					}
					}
				}
				
				//look at the face up cards to see if theres a rainbow
				for (int i = 0; i < this.getFaceUpCards().size(); i++) {
					if (this.getFaceUpCards().get(i).getColor().equals(TrainCardColor.rainbow)&&choke==false) {
						super.drawTrainCard(i + 1);
						choke = true;
					}
				}
				
				//look to see if I'm two away from buying a route on my path
				for (int i = 0; i < path.size(); i++) {
					int mycolor = this.getNumTrainCardsByColor(path.get(i).getColor());
				if (mycolor + 2 + numofRainBow >= path.get(i).getCost()&&path.get(i).getOwner()==null&&choke==false) {
					for (int j = 0; j < this.getFaceUpCards().size(); j++) {
						if (this.getFaceUpCards().get(j).getColor().equals(path.get(i).getColor())) {
							super.drawTrainCard(j + 1);
							choke=true;
						}
					}
				}
				}
				for (int i = 0; i < path.size(); i++) {
					int mycolor = this.getNumTrainCardsByColor(path.get(i).getColor());
					for (int j = 0; j < this.getFaceUpCards().size(); j++) {
						if (this.getFaceUpCards().get(j).getColor().equals(path.get(i).getColor())&&path.get(i).getOwner()==null) {
							super.drawTrainCard(j + 1);
							choke=true;
						}
				}
				}
				
				if(choke==false)
				super.drawTrainCard(0);
				
				//}
				}//The end of drawing
			}

		}

	//}

	public void endPath(ArrayList<Destination> mypath) {

		for (int i = 0; i < mypath.size() - 1; i++) {
			ArrayList<Route> keep = Routes.getInstance().getRoutes(
					mypath.get(i), mypath.get(i + 1));
			// ask about the multiple color
			endpaths.add(keep.get(0));
		}
		// order them from greatest length
		Collections.sort(endpaths, end);
	}
	
	public void Path(ArrayList<Destination> mypath) {

		for (int i = 0; i < mypath.size() - 1; i++) {
			ArrayList<Route> keep = Routes.getInstance().getRoutes(
					mypath.get(i), mypath.get(i + 1));
			// ask about the multiple color
			path.add(keep.get(0));
		}
		// order them from greatest length
		Collections.sort(path, com);
	}

	// Use this to fill my path array with routes
	public ArrayList<Destination> getPath(Destination from, Destination to) {
		/* If same, just return false */
		if (from == to)
			return null;

		/* Open and Closed lists (breadth first search) */
		HashMap<Destination, Destination> rou = new HashMap<Destination, Destination>();
		HashMap<Destination, Integer> openList = new HashMap<Destination, Integer>();
		HashMap<Destination, Integer> closedList = new HashMap<Destination, Integer>();
		ArrayList<Destination> back = new ArrayList<Destination>();

		openList.put(from, 0);
		boolean in = false;

		while (openList.size() > 0) {

			/* Pop something off the open list, if destination then return true */
			Destination next = null;
			int minCost = 9999;
			for (Destination key : openList.keySet()) {
				if (openList.get(key) < minCost) {
					next = key;
					node g = new node(from, next, openList.get(key));

					minCost = openList.get(key);
				}

			}

			/* Take it off the open list and put on the closed list */
			openList.remove(next);
			closedList.put(next, minCost);

			/* If this is the destination, then return!!!! */
			if (next == to) {
				back.add(next);
				while (!back.contains(from)) {
					back.add(rou.get(next));
					next = rou.get(next);
				}
				return back;
			}
			;

			// return closedList.get(next);
			/*
			 * Get all the neighbors of the next city that aren't on open or
			 * closed lists already
			 */
			for (Destination neighbor : Routes.getInstance().getNeighbors(next)) {
				if (closedList.containsKey(neighbor))
					continue;

				/*
				 * get route between next and neighbor and see if better than
				 * neighbor's value
				 */

				ArrayList<Route> routesToNeighbor = Routes.getInstance()
						.getRoutes(next, neighbor);
				for (Route routeToNeighbor : routesToNeighbor) {
					int newCost = closedList.get(next)
							+ routeToNeighbor.getCost();
					if (routeToNeighbor.getOwner() == this
							|| routeToNeighbor.getOwner() == null) {
						if (openList.containsKey(neighbor)) {
							if (newCost < openList.get(neighbor)) {
								openList.put(neighbor, newCost);
								rou.put(neighbor, next);
							}
						} else {
							openList.put(neighbor, newCost);
							rou.put(neighbor, next);
						}
					}
				}
			}
		}
		return back;

		// return 0;
	}

}

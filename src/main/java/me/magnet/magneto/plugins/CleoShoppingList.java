package me.magnet.magneto.plugins;

import java.util.ArrayList;
import java.util.Random;

import me.magnet.magneto.ChatRoom;
import me.magnet.magneto.annotations.RespondTo;
import me.magnet.magneto.annotations.Param;

public class CleoShoppingList implements MagnetoPlugin {
    ArrayList<String> thelist = new ArrayList<String>();
	public static final String[] LISTS = {
	  "Apples, Chicken and Beer",
	  "Wine and beer",
	  "Beer and tities, la la la la la."
	};


	public static final String[] WELCOMES = {
	  "Hi!",
	  "Hello",
	  "Welcome!",
	  "Good day to you!"
	};

	public static final String[] RULES = {
	  "1. A robot may not injure a human being or, through inaction, allow a human being to come to harm.",
	  "2. A robot must obey any orders given to it by human beings, except where such orders would conflict with the First Law.",
	  "3. A robot must protect its own existence as long as such protection does not conflict with the First or Second Law."
	};

	private final Random randomGen = new Random();

	/**
	 * Give a list of items that need to be bought
	 */
	@RespondTo(regex = "\\b(give me a list|what do we need|wat heb ik nodig|wat hebben we nodig|boodschappen|give me the list).*")
	public void giveList(ChatRoom room) {
//		int random = randomGen.nextInt(LISTS.length);
//		room.sendMessage("We need:" +LISTS[random]);
        room.sendMessage("We need " + thelist.size() +" things: ");
        for(String item:thelist){
            room.sendMessage(" - " + item);
        }
	}

    /**
     * add An item to the list
     * @return
     */
    @RespondTo(regex = "\\badd {item}")
    public void add(ChatRoom room, @Param("item") String item){
        thelist.add(item);
        room.sendMessage("Ok, I added "+ item + " to the list");
    }

    /**
     * remove an item
     * @return
     */
    @RespondTo(regex ="\\bremove {item}")
    public void remove(ChatRoom room, @Param("item") String item){
        thelist.remove(item);
        room.sendMessage("Ok, I remove "+ item + " to the list");
    }

    /**
     * remove all item
     * @return
     */
    @RespondTo(regex ="\\bremove everything")
    public void removeAll(ChatRoom room){
        thelist.clear();
        room.sendMessage("Ok, I cleared the list.");
    }


	@Override
	public String getName() {
		return "Cleo shoppinglist";
	}
}

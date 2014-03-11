package me.magnet.magneto.plugins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.magnet.magneto.ChatRoom;
import me.magnet.magneto.annotations.RespondTo;
import me.magnet.magneto.annotations.Param;

@Slf4j
public class CleoShoppingList implements MagnetoPlugin {
    private final File shoppingList;
    ArrayList<String> thelist = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();


    public CleoShoppingList() throws IOException {
        shoppingList = new File("shoppinglist.json");
        log.info("Loading {}", shoppingList.getAbsoluteFile());

        if (!shoppingList.exists()) {
            thelist = new ArrayList<>();
            shoppingList.createNewFile();
            log.info("The shoppinglist didn't exist yet, created one.");
            writeListToFile();
        } else {
            thelist = (ArrayList<String>) readShoppinglist();
        }
    }

    private List<String> readShoppinglist() {
        try {
            return mapper.readValue(shoppingList,
                    new TypeReference<List<String>>() {
                    });
        } catch (IOException e) {
            log.error("Shopping list file could not be read, cant save changes: " + e);
            return new ArrayList<String>();
        }

    }

    private void writeListToFile() throws IOException {
        log.info("Wrote shopping list to file");
        mapper.writerWithDefaultPrettyPrinter().writeValue(shoppingList, thelist);
    }

    /**
     * Give a list of items that need to be bought
     */
    @RespondTo(regex = "\\b(give me a list|what do we need|wat heb ik nodig|wat hebben we nodig|boodschappen|give me the list|list me).*")
    public void giveList(ChatRoom room) {
        String message;
        if (thelist.size() > 0) {
            message = "We need " + thelist.size() + " things:\n";
            int index = 1;
            for (String item : thelist) {
                message += index + ". " + item + "\n";
                index++;
            }
        } else {
            message = "We don't need anything.";
        }
        room.sendMessage(message);
    }

    /**
     * add An item to the list
     *
     * @return
     */
    @RespondTo(regex = "\\badd {item}")
    public void add(ChatRoom room, @Param("item") String item) {
        item = item.replace("and", ",");
        item = item.replace("en", ",");
        ArrayList<String> itemStrings = new ArrayList<String>(Arrays.asList(item.split(",")));
        thelist.addAll(itemStrings);
        try {
            writeListToFile();
            String plural = (itemStrings.size() > 1) ? "s" : "";
            room.sendMessage("Ok, I added " + itemStrings.size() + " item" + plural + " to the list");
        } catch (IOException e) {
            e.printStackTrace();
            room.sendMessage("Hmmm, I can remember the list right now, but I was unable to write it down..");
        }
    }

    /**
     * remove an item
     *
     * @return
     */
    @RespondTo(regex = "\\bremove {item}")
    public void remove(ChatRoom room, @Param("item") String item) {
        Boolean success;
        try {
            //If item is an int interpret it as an index
            int index = Integer.parseInt(item) - 1;
            item = thelist.remove(index); //Reuse item to let cleo tell what was removed.
            success = item != null; //store if anything was removed
        } catch (NumberFormatException e) {
            //If its not try to remove it as an object.
            success = thelist.remove(item);
        } catch (IndexOutOfBoundsException e) {
            success = false;
        }

        try {
            if (success) {
                writeListToFile();
                room.sendMessage("Ok, I removed " + item + " from the list");
            } else {
                room.sendMessage("Sorry, I couldn't remove what you wanted to.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            room.sendMessage("Hmmm, I can remember that you removed this right now, but I was unable to write it down..");
        }

    }

    /**
     * remove all item
     *
     * @return
     */
    @RespondTo(regex = "\\bclear list:?")
    public void removeAll(ChatRoom room) {
        try {
            thelist.clear();
            room.sendMessage("Ok, I cleared the list.");
            writeListToFile();
        } catch (IOException e) {
            e.printStackTrace();
            room.sendMessage("Hmmm, I can remember that you removed this right now, but I was unable to write it down..");
        }

    }


    @Override
    public String getName() {
        return "Cleo shoppinglist";
    }
}

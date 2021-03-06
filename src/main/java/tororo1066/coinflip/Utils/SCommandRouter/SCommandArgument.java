package tororo1066.coinflip.Utils.SCommandRouter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SCommandArgument {
    ArrayList<String> alias = new ArrayList<>();
    ArrayList<String> allowedStrings = new ArrayList<>();

    public ArrayList<String> explanation = new ArrayList<>();

    public SCommandArgument addExplanation(String text){
        explanation.add(text);
        return this;
    }

    ArrayList<SCommandArgumentType> allowedTypes = new ArrayList<>();

    public SCommandArgument addAllowedString(String string){
        allowedStrings.add(string);
        alias.add(string);
        return this;
    }

    public SCommandArgument addAlias(String string){
        this.alias.add(string);
        return this;
    }

    public SCommandArgument addAllowedType(SCommandArgumentType type){
        allowedTypes.add(type);
        if(type == SCommandArgumentType.BOOLEAN){
            alias.add("true");
            alias.add("false");
        }
        if (type == SCommandArgumentType.HEADSORTAILS){
            alias.add("heads");
            alias.add("tails");
            alias.add("表");
            alias.add("裏");
        }
        return this;
    }

    public boolean hasType(SCommandArgumentType type){
        return allowedTypes.contains(type);
    }

    public boolean matchesType(String arg){
        for(SCommandArgumentType type: allowedTypes){
            try{
                switch (type){
                    case BOOLEAN:
                        if(!"true".equalsIgnoreCase(arg) && !"false".equalsIgnoreCase(arg)){
                            return false;
                        }
                        return true;
                    case INT:
                        Integer.parseInt(arg);
                        return true;
                    case DOUBLE:
                        Double.parseDouble(arg);
                        return true;
                    case ONLINE_PLAYER:
                        Player p = Bukkit.getServer().getPlayer(arg);
                        if(p == null) return false;
                        if(!p.getName().equals(arg)) return false;
                        if(!p.isOnline()) return false;
                        return true;
                    case WORLD:
                        for(World w: Bukkit.getServer().getWorlds()){
                            if(w.getName().equals(arg)) return true;
                        }
                        return false;
                    case HEADSORTAILS:
                        if (!"heads".equalsIgnoreCase(arg) && !"tails".equalsIgnoreCase(arg) && !"表".equalsIgnoreCase(arg) && !"裏".equalsIgnoreCase(arg)){
                            return false;
                        }
                        return true;
                }
            }catch (Exception e){
                return false;
            }
        }
        return true;
    }

    public boolean matches(String arg){
        if(!matchesType(arg)) return false;
        if(allowedStrings.size() != 0 && !allowedStrings.contains(arg)) return false;
        return true;
    }


}

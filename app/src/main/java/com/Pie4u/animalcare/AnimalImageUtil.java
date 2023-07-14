package com.Pie4u.animalcare;

import java.util.HashMap;
import java.util.Map;

public class AnimalImageUtil {

    private Map<String,Integer> resourceIdMap = new HashMap<>();

    public AnimalImageUtil(){
        resourceIdMap.put("Dog",R.drawable.moti);
        resourceIdMap.put("Cat",R.drawable.mani);
        resourceIdMap.put("Snake",R.drawable.snake);
        resourceIdMap.put("Cow",R.drawable.cow);
    }

    public int getAnimalImageResourceId(String animalTypeKey){

        if(resourceIdMap.get(animalTypeKey)!=null)
            return resourceIdMap.get(animalTypeKey);
        return -1;
    }
}

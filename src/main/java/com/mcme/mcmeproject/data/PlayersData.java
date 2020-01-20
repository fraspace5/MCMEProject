/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcme.mcmeproject.data;

import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author Fraspace5
 */
public class PlayersData {

    public HashMap<UUID, Integer> r;

    public Long lastplayed;

    public PlayersData(HashMap ss, Long l) {

        r = ss;

        lastplayed = l;
    }

}

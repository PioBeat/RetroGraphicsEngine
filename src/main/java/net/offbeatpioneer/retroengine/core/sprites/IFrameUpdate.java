package net.offbeatpioneer.retroengine.core.sprites;

/**
 * Created by Dome on 15.01.2017.
 */

public interface IFrameUpdate {

    /**
     * Aktualisierungsschritt. Falls ein Filmstreifen vorliegt, dann wird bei jedem Frame-Update das n�chste Bild ausgew�hlt.
     *
     * @return aktuelle Position im Filmstreifen.
     */
    public int updateFrame();
}

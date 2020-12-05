package org.anvilpowered.datasync.api.plugin;

public interface PluginMessages<TString> {

    TString getNoPermissions();

    TString getUserRequired();

    TString getInvalidUser();
}

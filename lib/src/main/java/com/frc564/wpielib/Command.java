package com.frc564.wpielib;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Command {
    private final HashSet<Subsystem> _REQUIREMENTS = new HashSet<>();
    private String _name = null;

    public void initialize() { }
    public void execute() { }
    public void end(boolean interrupt) { }
    public boolean isFinished() { return true; }

    public String getName() { return (_name == null) ? this.getClass().getName() : _name; }
    public void setName(String name) { _name = name; }

    public final Set<Subsystem> getRequirements() { return _REQUIREMENTS; }

    public final void addRequirements(Collection<Subsystem> requirements) {
        _REQUIREMENTS.addAll(requirements);
    }
}

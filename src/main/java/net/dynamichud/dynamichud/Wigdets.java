package net.dynamichud.dynamichud;

import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.Widget;

import java.util.ArrayList;
import java.util.List;

public interface Wigdets {
    List<Widget> widgets=new ArrayList<>();
    void addWigdets(DynamicUtil dynamicUtil);
}

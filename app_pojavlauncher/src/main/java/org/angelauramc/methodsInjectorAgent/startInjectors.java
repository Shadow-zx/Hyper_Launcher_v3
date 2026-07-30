package org.angelauramc.methodsInjectorAgent;

import org.angelauramc.methodsInjectorAgent.lwjgl2_methods_injector.ALC10Injector;
import org.angelauramc.methodsInjectorAgent.lwjgl2_methods_injector.ASM5OverrideInjector;
import org.angelauramc.methodsInjectorAgent.mods_compatibility_injector.VeilImguiOverrideDisable;

public class startInjectors {
    public static void premain(String args, Object inst) {
        try {
            // Check if we have the asm classes we need
            Class<?> opcodesClass = Class.forName("org.objectweb.asm.Opcodes");
            Package asmPackage = opcodesClass.getPackage();
            String implVersion = asmPackage.getImplementationVersion();
            if (implVersion == null) implVersion = "not found";
            System.out.println("Amethyst-Android: Detected ASM version: " + implVersion);
            ALC10Injector.premain(args, inst);
            VeilImguiOverrideDisable.premain(args, inst);
            // This is the version we override old asm vers with. So we add the patches
            // so the older version bugs are ported.
            if ("5.0.4".equals(implVersion)) ASM5OverrideInjector.premain(args, inst);
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
    }
}

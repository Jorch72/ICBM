package com.builtbroken.icbm.content.blast.potion;

import com.builtbroken.icbm.api.blast.IBlastHandler;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveData;
import com.builtbroken.mc.framework.explosive.handler.ExplosiveHandler;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 1/30/2016.
 */
public class ExFlash extends ExplosiveHandler<BlastFlash> implements IBlastHandler
{
    /**
     * Creates an explosive using a blast class, and name
     */
    public ExFlash(ExplosiveData data)
    {
        super(data);
    }

    @Override
    protected BlastFlash newBlast()
    {
        return new BlastFlash(this);
    }
}

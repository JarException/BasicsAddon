package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.addons.basics.utils.FuelJerrycanUtils;
import fr.dynamx.api.contentpack.object.part.InteractivePart;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.loader.ModularVehicleInfoBuilder;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.function.Predicate;

public class FuelTankInfos extends InteractivePart<BaseVehicleEntity<?>, ModularVehicleInfoBuilder> {

    @PackFileProperty(configNames = "TankSize", type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "100")
    protected float tankSize;

    @PackFileProperty(configNames = "FuelConsumption", type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "1")
    protected float fuelConsumption;

    public FuelTankInfos(ModularVehicleInfoBuilder owner, String partName) {
        super(owner, partName, 0.5f, 0.5f);
    }

    public float getTankSize() {
        return tankSize;
    }

    public float getFuelConsumption() {
        return fuelConsumption;
    }

    @Override
    public void appendTo(ModularVehicleInfoBuilder owner) {
        super.appendTo(owner);
    }

    @Override
    public boolean interact(BaseVehicleEntity<?> entity, EntityPlayer with) {
        if(FuelJerrycanUtils.isJerrycanItem(with.getHeldItemMainhand()))
        {
            BasicsItemInfo jerrycan = ((ItemObject<?>) ((DynamXItem<?>) with.getHeldItemMainhand().getItem()).getInfo()).getSubPropertyByType(BasicsItemInfo.class);
            if (!FuelJerrycanUtils.isFuel(with.getHeldItemMainhand())) {
                FuelJerrycanUtils.setFuel(with.getHeldItemMainhand(), jerrycan.fuelCapacity);
            }
            else
            {
                FuelTankModule tank = entity.getModuleByType(FuelTankModule.class);
                if(tank != null)
                {
                    FuelJerrycanUtils.setFuel(with.getHeldItemMainhand(), (int) (FuelJerrycanUtils.getFuel(with.getHeldItemMainhand())-(tank.getInfo().getTankSize()-tank.getFuel())));
                    tank.setFuel(tank.getFuel()+Math.min(tank.getInfo().getTankSize(), tank.getInfo().getTankSize()-tank.getFuel()));
                }
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "FuelTank of BlackNite";
    }

    @Override
    public String getPackName() {
        return "FuelTank";
    }

    @Override
    public void addModules(BaseVehicleEntity<?> entity, ModuleListBuilder moduleListBuilder) {
        if (moduleListBuilder.hasModuleOfClass(FuelTankModule.class)) { //Module yet added
            throw new IllegalStateException("More than one fuel tank infos ("+getFullName()+") added to "+entity.getPackInfo().getFullName()+" "+entity);
        } else { //Module not yet added
            moduleListBuilder.add(new FuelTankModule(entity,this));
        }
    }
}
package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.random.RandomChanceSkill;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AcrobaticsCommand extends SkillCommand {
    private String dodgeChance;
    private String dodgeChanceLucky;

    private boolean canDodge;
    private boolean canRoll;

    public AcrobaticsCommand(mcMMO pluginRef) {
        super(PrimarySkillType.ACROBATICS, pluginRef);
    }

    @Override
    protected void dataCalculations(Player player, double skillValue) {
        // ACROBATICS_DODGE
        if (canDodge) {
            String[] dodgeStrings = getAbilityDisplayValues(player, SubSkillType.ACROBATICS_DODGE);
            dodgeChance = dodgeStrings[0];
            dodgeChanceLucky = dodgeStrings[1];
        }
    }

    @Override
    protected void permissionsCheck(Player player) {
        canDodge = canUseSubSkill(player, SubSkillType.ACROBATICS_DODGE);
        canRoll = canUseSubSkill(player, SubSkillType.ACROBATICS_ROLL);
    }

    @Override
    protected List<String> statsDisplay(Player player, double skillValue, boolean hasEndurance, boolean isLucky) {
        List<String> messages = new ArrayList<>();

        if (canDodge) {
            messages.add(getStatMessage(SubSkillType.ACROBATICS_DODGE, dodgeChance)
                    + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", dodgeChanceLucky) : ""));
        }

        if (canRoll) {

            AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName("Roll");

            if (abstractSubSkill != null) {
                double rollChance, graceChance;

                //Chance to roll at half
                RandomChanceSkill roll_rcs = new RandomChanceSkill(pluginRef, player, SubSkillType.ACROBATICS_ROLL);

                //Chance to graceful roll
                RandomChanceSkill grace_rcs = new RandomChanceSkill(pluginRef, player, SubSkillType.ACROBATICS_ROLL);
                grace_rcs.setSkillLevel(grace_rcs.getSkillLevel() * 2); //Double Odds

                //Chance Stat Calculations
                rollChance = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(roll_rcs);
                graceChance = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(grace_rcs);
                //damageThreshold  = AdvancedConfig.getInstance().getRollDamageThreshold();

                String[] rollStrings = getAbilityDisplayValues(player, SubSkillType.ACROBATICS_ROLL);

                //Format
                double rollChanceLucky = rollChance * 1.333D;
                double graceChanceLucky = graceChance * 1.333D;

                messages.add(getStatMessage(SubSkillType.ACROBATICS_ROLL, rollStrings[0])
                        + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", rollStrings[1]) : ""));

                /*messages.add(getStatMessage(true, false, SubSkillType.ACROBATICS_ROLL, String.valueOf(graceChance))
                        + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", String.valueOf(graceChanceLucky)) : ""));*/
            }
        }

        return messages;
    }

    @Override
    protected List<TextComponent> getTextComponents(Player player) {
        List<TextComponent> textComponents = new ArrayList<>();

        pluginRef.getTextComponentFactory().getSubSkillTextComponents(player, textComponents, PrimarySkillType.ACROBATICS);

        return textComponents;
    }
}
package org.inspirerobotics.sumobots.field.util;

import org.inspirerobotics.sumobots.field.FieldBackend;
import org.inspirerobotics.sumobots.library.InternalLog;
import org.inspirerobotics.sumobots.library.TimePeriod;
import org.inspirerobotics.sumobots.library.concurrent.InterThreadMessage;
import org.inspirerobotics.sumobots.library.networking.message.ArchetypalMessages;

import java.util.logging.Logger;

public class MatchController {

	private static final Logger log = InternalLog.getLogger();
	private TimePeriod currentTimePeriod;
	private FieldBackend fieldBackend;

	public MatchController(FieldBackend fieldBackend) {
		this.fieldBackend = fieldBackend;
		this.currentTimePeriod = TimePeriod.DISABLED;
	}

	public void attemptStateChange(TimePeriod newPeriod) {
		if (!verifyStateChange(currentTimePeriod, newPeriod))
			return;

		changeState(newPeriod);
	}

	private void changeState(TimePeriod newPeriod) {
		this.currentTimePeriod = newPeriod;

		InterThreadMessage m = new InterThreadMessage("time_period_update", newPeriod);
		fieldBackend.sendMessageToFrontend(m);

		fieldBackend.getServer().sendAll(ArchetypalMessages.enterNewMatchPeriod(newPeriod));
	}

	public static boolean verifyStateChange(TimePeriod oldPeriod, TimePeriod newPeriod) {
		if (newPeriod == TimePeriod.INIT && oldPeriod != TimePeriod.DISABLED) {
			log.warning("Match cannot be initialized from a non-disabled state!");
			return false;
		} else if (newPeriod != TimePeriod.ESTOPPED && oldPeriod == TimePeriod.ESTOPPED) {
			log.warning("Match state cannot be changed from e-stop!");
			return false;
		} else if (newPeriod == TimePeriod.GAME && oldPeriod != TimePeriod.INIT) {
			log.warning("Match cannot be started from a non-initialized state!");
			return false;
		}
		return true;
	}

}

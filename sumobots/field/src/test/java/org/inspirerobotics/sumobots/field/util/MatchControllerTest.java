package org.inspirerobotics.sumobots.field.util;

import org.inspirerobotics.sumobots.library.TimePeriod;
import org.junit.Assert;
import org.junit.Test;

public class MatchControllerTest {

	@Test
	public void verifyStateChangeFailFromEStopToOther() {
		for (TimePeriod timePeriod : TimePeriod.values()) {
			if (timePeriod == TimePeriod.ESTOPPED)
				continue;

			boolean result = MatchController.verifyStateChange(TimePeriod.ESTOPPED, timePeriod);

			Assert.assertFalse(result);
		}
	}

	@Test
	public void verifyStateChangeEStopAlwaysSucceeds() {
		for (TimePeriod timePeriod : TimePeriod.values()) {
			Assert.assertTrue(MatchController.verifyStateChange(timePeriod, TimePeriod.ESTOPPED));
		}
	}

	@Test
	public void verifyStateChangeSucceedFromInitToDisable() {
		Assert.assertTrue(MatchController.verifyStateChange(TimePeriod.INIT, TimePeriod.DISABLED));
	}

	@Test
	public void verifyStateChangeSucceedFromGameToDisable() {
		Assert.assertTrue(MatchController.verifyStateChange(TimePeriod.GAME, TimePeriod.DISABLED));
	}

	@Test
	public void verifyStateChangeFailFromDisableToGame() {
		Assert.assertFalse(MatchController.verifyStateChange(TimePeriod.DISABLED, TimePeriod.GAME));
	}

	@Test
	public void verifyStateChangeSuccessFromInitToGame() {
		Assert.assertTrue(MatchController.verifyStateChange(TimePeriod.INIT, TimePeriod.GAME));
	}

}

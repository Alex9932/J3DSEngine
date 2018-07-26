package alex9932.utils;

public class Timer {
	float ticksPerSecond;
	private double lastHRTime;
	public int elapsedTicks;
	public float renderPartialTicks;
	public float timerSpeed = 1.0F;
	public float elapsedPartialTicks = 0.0F;
	private long lastSyncSysClock;
	private long lastSyncHRClock;
	private long field_74285_i;
	private double timeSyncAdjustment = 1.0D;

	public Timer(float par1) {
		this.ticksPerSecond = par1;
		this.lastSyncSysClock = getSystemTime();
		this.lastSyncHRClock = System.currentTimeMillis();
	}
	
	private long getSystemTime() {
		return System.currentTimeMillis();
	}

	public void updateTimer() {
		long var1 = getSystemTime();
		long var3 = var1 - this.lastSyncSysClock;
		long var5 = System.currentTimeMillis();
		double var7 = (double) var5 / 1000.0D;

		if (var3 <= 1000L && var3 >= 0L) {
			this.field_74285_i += var3;

			if (this.field_74285_i > 1000L) {
				long var9 = var5 - this.lastSyncHRClock;
				double var11 = (double) this.field_74285_i / (double) var9;
				this.timeSyncAdjustment += (var11 - this.timeSyncAdjustment) * 0.20000000298023224D;
				this.lastSyncHRClock = var5;
				this.field_74285_i = 0L;
			}

			if (this.field_74285_i < 0L) {
				this.lastSyncHRClock = var5;
			}
		} else {
			this.lastHRTime = var7;
		}

		this.lastSyncSysClock = var1;
		double var13 = (var7 - this.lastHRTime) * this.timeSyncAdjustment;
		this.lastHRTime = var7;

		if (var13 < 0.0D) {
			var13 = 0.0D;
		}

		if (var13 > 1.0D) {
			var13 = 1.0D;
		}

		this.elapsedPartialTicks = (float) ((double) this.elapsedPartialTicks + var13 * (double) this.timerSpeed * (double) this.ticksPerSecond);
		this.elapsedTicks = (int) this.elapsedPartialTicks;
		this.elapsedPartialTicks -= (float) this.elapsedTicks;

		if (this.elapsedTicks > 10) {
			this.elapsedTicks = 10;
		}

		this.renderPartialTicks = this.elapsedPartialTicks;
	}

	public float getTps() {
		return ticksPerSecond - elapsedTicks;
	}
}
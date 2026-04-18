package dev.winclip.vetclinic.doctor.dto;

public enum WeekdayCode {
	MON,
	TUE,
	WED,
	THU,
	FRI,
	SAT,
	SUN;

	public static WeekdayCode fromIsoDayOfWeek(int dayOfWeek) {
		if (dayOfWeek < 1 || dayOfWeek > 7) {
			throw new IllegalArgumentException("dayOfWeek must be between 1 and 7, got " + dayOfWeek);
		}
		return values()[dayOfWeek - 1];
	}
}

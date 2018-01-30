package de.heinzen.visualization.lightbot;

import de.prob.translator.types.BigInteger;
import de.prob.translator.types.Tuple;

import java.util.*;

/**
 * @author Christoph Heinzen
 * @version 0.1.0
 * @since 09.11.17
 */
public class Position {

	public static Map<Position, Integer> to3DPositionMap(Object level) {
		if (level instanceof Set) {
			Set levelSet = (Set) level;
			Map<Position, Integer> positions = new HashMap<>(levelSet.size());
			for (Object levelSetElement : levelSet) {
				if (levelSetElement instanceof Tuple) {
					Tuple tuple1 = (Tuple) levelSetElement;
					if (tuple1.getFirst() instanceof Tuple && tuple1.getSecond() instanceof BigInteger) {
						Tuple tuple2 = (Tuple) tuple1.getFirst();
						BigInteger z = (BigInteger) tuple1.getSecond();
						if (tuple2.getFirst() instanceof BigInteger && tuple2.getSecond() instanceof BigInteger) {
							BigInteger x = (BigInteger) tuple2.getFirst();
							BigInteger y = (BigInteger) tuple2.getSecond();
							positions.put(new Position(x.intValue(), y.intValue()), z.intValue());
						}
					}
				}
			}
			return positions;
		}
		return null;
	}

	public static List<Position> toPositionList(Object positionsObject) {
		if (positionsObject instanceof Set) {
			Set positionsSet = (Set) positionsObject;
			List<Position> positions = new ArrayList<>(positionsSet.size());
			for (Object levelSetElement : positionsSet) {
				if (levelSetElement instanceof Tuple) {
					Tuple tuple1 = (Tuple) levelSetElement;
					if (tuple1.getFirst() instanceof BigInteger && tuple1.getSecond() instanceof BigInteger) {
						BigInteger x = (BigInteger) tuple1.getFirst();
						BigInteger y = (BigInteger) tuple1.getSecond();
						positions.add(new Position(x.intValue(), y.intValue()));
					}
				}
			}
			return positions;
		}
		return null;
	}

	private int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Position position = (Position) o;

		if (x != position.x) return false;
		return y == position.y;
	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		return result;
	}
}

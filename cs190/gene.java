package cs190;

public class gene{
	private boolean turbine;
	private double power;
	private boolean[][] wake;
	private double[] u;
	protected int ipos, jpos, row, col;
	private boolean isMultipleWindSpeed;
	
	public gene(int row, int col, int ipos, int jpos, boolean multSpeed) {
		this.isMultipleWindSpeed = multSpeed;
		wake = new boolean[row][col];
		turbine = false;
		power = 0;
		u = new double[] {Double.NaN, Double.NaN, Double.NaN};
		this.ipos = ipos;
		this.jpos = jpos;
		this.row = row;
		this.col = col;
	}
	
	protected boolean isTurbineUnderWake(int i, int j) {
		return wake[i][j];
	}
	
	protected boolean isTurbinePresent() {
		return turbine;
	}
	
	protected void setTurbinePresence(boolean b) {
		turbine = b;
	}
	
	protected double[] getWindSpeed() {
		return u;
	}
	
	protected void setWindSpeed(double[] u, int theta) {
		this.u = u;
		power = 0;
		for(double windspeed : u) {
			power += 0.3*Math.pow(windspeed, 3)*getFractionOfOccurence(windspeed, theta);
		}
	}
	
	private double getFractionOfOccurence(double windspeed, int theta) {
		if(isMultipleWindSpeed) {
			double[][] data = new double[3][36];
			for(int i=0;i<data.length;i++) {
				for(int j=0;j<data[i].length;j++){
					if(i == 0) {
						data[i][j] = 0.004;
					} else if(i == 1) {
						if(j >= 0 && j <= 26) {
							data[i][j] = 0.008;
						} else if(j == 27 || j == 35) {
							data[i][j] = 0.011;
						} else if(j == 28 || j == 34) {
							data[i][j] = 0.013;
						} else if(j == 29 || j == 33) {
							data[i][j] = 0.015;
						} else if(j == 30 || j == 32) {
							data[i][j] = 0.0145;
						} else {
							data[i][j] = 0.02;
						}
					} else {
						if(j >= 0 && j <= 26) {
							data[i][j] = 0.012;
						} else if(j == 27 || j == 35) {
							data[i][j] = 0.013;
						} else if(j == 28 || j == 34) {
							data[i][j] = 0.014;
						} else if(j == 29 || j == 33) {
							data[i][j] = 0.02;
						} else if(j == 30 || j == 32) {
							data[i][j] = 0.03;
						} else {
							data[i][j] = 0.035;
						}
					}
				}
			}
			
			
			
			if(windspeed <= 8) {
				return data[0][(int) theta/10];
			} else if(windspeed <= 12) {
				return data[1][(int) theta/10];
			} else {
				return data[2][(int) theta/10];
			}
		} else {
			//System.out.println("wrong");
			return (double) 1/36;
		}
	}

	protected double getPower() {
		return power;
	}
	
	protected void computeWake(int i0, int j0, int theta) {
		wake = new boolean[row][col];
		int iMin = 0, iMax = wake.length, jMin = 0, jMax = wake[0].length;
		double z = 20/Math.tan(Math.toRadians(10)) / 200;
		double dx, dy;
		
		switch(theta){
			case 90:
				dx = 0;
				dy = -z;
				break;
			case 180:
				dx = -z;
				dy = 0;
				break;
			case 270:
				dx = 0;
				dy = z;
				break;
			default:
				dx = z*Math.cos(Math.toRadians((-1)*theta));
				dy = z*Math.sin(Math.toRadians((-1)*theta));
		}
		
		//Sets the area of coverage of the wake
		if(theta == 0) {
			iMin = i0;
		}else if(theta > 0 && theta < 90){
			iMin = i0;
			jMax = j0;
		}else if(theta == 90) {
			jMax = j0;
		}else if(theta > 90 && theta < 180) {
			iMax = i0;
			jMax = j0;
		}else if(theta == 180) {
			iMax = i0;
		}else if(theta > 180 && theta < 270) {
			iMax = i0;
			jMin = j0;
		}else if(theta == 270) {
			jMin = j0;
		}else{
			iMin = i0;
			jMin = j0;
		}
		
		for(int i=0;i<row;i++) {
			for(int j=0;j<col;j++) {
				//Compute the angle
				if(i != i0 || j != j0) {
					if(i >= iMin && i <= iMax && j >= jMin && j <= jMax) {
						try {
							
							double angle = 0;
							double diffangle = 0;
							if((theta>=0 && theta<90) || (theta>=180 && theta<270)) {
								angle = Math.toDegrees(Math.atan( ((float) j-j0+dy)/((float) i-i0+dx) ));
								diffangle = Math.abs(angle+theta-(90*Math.floor(theta/90.0)));
							}else {
								angle = Math.toDegrees(Math.atan( ((float) i-i0+dx)/((float) j-j0+dy) ));
								diffangle = Math.abs(angle-(theta-(90*Math.floor(theta/90.0))));
							}
							
							wake[i][j] = (diffangle <= 10);
						} catch (Exception e) {
							System.out.print("null\t");
						}
					}else{
						wake[i][j] = false;
					}
				}
			}
		}
	}
}

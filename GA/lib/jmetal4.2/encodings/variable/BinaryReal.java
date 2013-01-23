//  BinaryReal.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
// 
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package encodings.variable;

import java.util.BitSet;

import core.Variable;
import util.JMException;
import util.PseudoRandom;
import util.Configuration.*;

/** This class extends the Binary class to represent a Real variable encoded by
 * a binary string
 */
public class BinaryReal extends Binary {
	
	/**
   * Defines the default number of bits used for binary coded variables.
   */	
	public static final int DEFAULT_PRECISION = 30; 
	
  /**
   * Stores the real value of the variable
   */
  private double value_;
  
  /**
   * Stores the lower limit for the variable
   */
  private double lowerBound_;
  
  /**
   * Stores the upper limit for the variable
   */
  private double upperBound_;  
     
  /**
   * Constructor.
   */
  public BinaryReal() {    
    super();
  } //BinaryReal

  /**
   * Constructor
   * @param numberOfBits Length of the binary string.
   * @param lowerBound The lower limit for the variable
   * @param upperBound The upper limit for the variable.
   */
  public BinaryReal(int numberOfBits, double lowerBound, double upperBound){
    super(numberOfBits);
    lowerBound_     = lowerBound;
    upperBound_     = upperBound;
    
    decode();                              
  } //BinaryReal
    
	/**
	 * @param bits
	 * @param nbBits
	 * @param lowerBound
	 * @param upperBound
	 */
	public BinaryReal(BitSet bits, int nbBits, double lowerBound, double upperBound) {
		super(nbBits);
		bits_ = bits;
		lowerBound_ = lowerBound;
		upperBound_ = upperBound;
		decode();
	} // BinaryReal

  /**
   * Copy constructor
   * @param variable The variable to copy
   */
  public BinaryReal(BinaryReal variable){
    super(variable);

    lowerBound_   = variable.lowerBound_;
    upperBound_   = variable.upperBound_;
    /*
    numberOfBits_ = variable.numberOfBits_;
     
    bits_ = new BitSet(numberOfBits_);
    for (int i = 0; i < numberOfBits_; i++)
      bits_.set(i,variable.bits_.get(i));
    */  
    value_ = variable.value_;
  } //BinaryReal
    
  
  /**
   * Decodes the real value encoded in the binary string represented
   * by the <code>BinaryReal</code> object. The decoded value is stores in the 
   * <code>value_</code> field and can be accessed by the method
   * <code>getValue</code>.
   */
  public void decode(){
    double value = 0.0;        
    for (int i = 0; i < numberOfBits_; i++) {
      if (bits_.get(i)) {
        value += Math.pow(2.0,i);
      }
    }
        
    value_ = value * (upperBound_ - lowerBound_) /
                                              (Math.pow(2.0,numberOfBits_)-1.0);        
    value_ += lowerBound_;    
  } //decode
 
  /**
   * Returns the double value of the variable.
   * @return the double value.
   */
  public double getValue() {
    return value_;
  } //getValue

	/**
	 * This implementation is efficient for binary string of length up to 24
	 * bits, and for positive intervals.
	 * 
	 * @see core.Variable#setValue(double)
	 * 
	 * @author jl hippolyte
	 */
	@Override
	public void setValue(double value) throws JMException {
		if (numberOfBits_ <= 24 && lowerBound_ >= 0) {
			BitSet bitSet;
			if (value <= lowerBound_) {
				 bitSet = new BitSet(numberOfBits_);
				bitSet.clear();
			} else if (value >= upperBound_) {
				 bitSet = new BitSet(numberOfBits_);
				bitSet.set(0, numberOfBits_);
			} else {
				 bitSet = new BitSet(numberOfBits_);
				bitSet.clear();
				// value is the integerToCode-th possible value, what is integerToCode?
				int integerToCode = 0;
				double tmp = lowerBound_;
				double path = (upperBound_ - lowerBound_) / (Math.pow(2.0, numberOfBits_) - 1);
				while (tmp < value) {
					tmp +=  path;
					integerToCode++;
				}
				int remain = integerToCode;
				for (int i = numberOfBits_ - 1; i >= 0; i--) {
					//System.out.println("i= " + i);
					int ithPowerOf2 = (int) Math.pow(2, i);

					if (ithPowerOf2 <= remain) {
						//System.out
					//			.println(ithPowerOf2thValue + " <= " + remain);
						bitSet.set(i);
						remain -= ithPowerOf2;
					} else {
						bitSet.clear(i);
					}
				}		
			}
			this.bits_ = bitSet;
			this.decode();
			
		} else {
			if (lowerBound_ < 0)
				throw new JMException("Unsupported lowerbound: " + lowerBound_
						+ " > 0");
			if (numberOfBits_>= 24)
				throw new JMException("Unsupported bit string length"
						+ numberOfBits_ + " is > 24 bits");
		}
	}// setValue
  
  /**
   * Creates an exact copy of a <code>BinaryReal</code> object.
   * @return The copy of the object
   */
  public Variable deepCopy() {
    return new BinaryReal(this);
  } //deepCopy

  /**
   * Returns the lower bound of the variable.
   * @return the lower bound.
   */
  public double getLowerBound() {
    return lowerBound_;
  } // getLowerBound

  /**
   * Returns the upper bound of the variable.
   * @return the upper bound.
   */
  public double getUpperBound() {
    return upperBound_;
  } // getUpperBound

  /**
   * Sets the lower bound of the variable.
   * @param lowerBound the lower bound.
   */
  public void setLowerBound(double lowerBound) {
    lowerBound_ = lowerBound;
  } // setLowerBound

  /**
   * Sets the upper bound of the variable.
   * @param upperBound the upper bound.
   */
  public void setUpperBound(double upperBound) {
    upperBound_ = upperBound;
  } // setUpperBound
  
  /**
   * Returns a string representing the object.
   * @return the string.
   */
  public String toString() {
    return value_+"";
  } // toString
} // BinaryReal

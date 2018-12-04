package nc_java;


import java.util.Arrays;



public class NC_JAVA {
	private static GF gf = new GF();

	private NC_JAVA() {
		//gf ;
	}

	// 矩阵相乘
	public static byte[][] Multiply(byte[][] mat1, byte[][] mat2) {
		int row = mat1.length;
		int k = mat1[0].length;
		int k1 = mat2.length;
		// 第一个矩阵列不等于第二个矩阵的行，不能相乘
		if (k != k1) {
			return null;
		}
		int col = mat2[0].length;
		byte[][] result = new byte[row][col];
		int temp;
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				temp = 0;
				for (int n = 0; n < k; ++n) {
					int a = mat1[i][n];
					if (a < 0) {
						a = 256 - Math.abs(a);
					}
					int b = mat2[n][j];
					if (b < 0) {
						b = 256 - Math.abs(b);
					}
					temp = gf.add(temp, gf.mul(a, b));
				}
				result[i][j] = (byte) temp;
			}
		}
		return result;
	}

	// 输入操作范围
	public static byte[][] Multiply(byte[][] mat1,int row1,int col1, byte[][] mat2,int row2,int col2) {
		int row = row1;
		int k = col1;
		int k1 = row2;
		// 第一个矩阵列不等于第二个矩阵的行，不能相乘
		if (k != k1) {
			return null;
		}

		int col = col2;
		byte[][] result = new byte[row][col];
		int temp;
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				temp = 0;
				for (int n = 0; n < k; ++n) {
					int a = mat1[i][n];
					if (a < 0) {
						a = 256 - Math.abs(a);
					}
					int b = mat2[n][j];
					if (b < 0) {
						b = 256 - Math.abs(b);
					}
					temp = gf.add(temp, gf.mul(a, b));
				}
				result[i][j] = (byte) temp;
			}
		}
		return result;
	}
	// 矩阵求逆
	public static byte[][] Inverse(byte[][] M) {
		int nRow = M.length;
		int nCol = M[0].length;
		if (nRow != nCol) {
			System.out.println("不是一个方阵");
			return null;
		}
		int[][] Mat = byteArray2IntArray(M);
		int nRank = getRank(Mat, nRow, nCol);
		// 求逆
		int bRet = nRank;
		if (bRet != nRow) {
			return null;
		} else {
			/************************************************************************/
			/** Start to get the inverse matrix! */
			/************************************************************************/

			int[][] N = new int[nCol][2 * nCol];
			for (int i = 0; i < nCol; i++) {
				for (int j = 0; j < nCol; j++) {
					N[i][j] = Mat[i][j];
				}
				for (int j = nCol; j < 2 * nCol; j++) {
					if (i == j - nCol) {
						N[i][j] = 1;
					} else {
						N[i][j] = 0;
					}
				}
			}
			/************************************************************************/
			/** Step 1. Change to a lower triangle matrix. */
			/************************************************************************/
			for (int i = 0; i < nCol; i++) {
				// There must exist a non-zero mainelement.
				if (N[i][i] == 0) {
					// Record this line.
					int[] temp = new int[200];
					Arrays.fill(temp, 0);
					for (int k = 0; k < 2 * nCol; k++) {
						temp[k] = N[i][k];
					}
					// Exchange
					int Row = nCol; // They are the same in essensial.
					for (int z = i + 1; z < Row; z++) {
						if (N[z][i] != 0) {
							for (int x = 0; x < 2 * nCol; x++) {
								N[i][x] = N[z][x];
								N[z][x] = temp[x];
							}
							break;
						}
					}
				}

				for (int j = i + 1; j < nCol; j++) {
					// Now, the main element must be nonsingular.
					int temp = gf.div(N[j][i], N[i][i]);
					for (int z = 0; z < 2 * nCol; z++) {
						N[j][z] = gf.add(N[j][z], gf.mul(temp, N[i][z]));
					}
				}
			}
			/************************************************************************/
			/** Step 2. Only the elements on the diagonal are non-zero. */
			/************************************************************************/
			for (int i = 1; i < nCol; i++) {
				for (int k = 0; k < i; k++) {
					int temp = gf.div(N[k][i], N[i][i]);
					for (int z = 0; z < 2 * nCol; z++) {
						N[k][z] = gf.add(N[k][z], gf.mul(temp, N[i][z]));
					}
				}
			}
			/************************************************************************/
			/* Step 3. The elements on the diagonal are 1. */
			/************************************************************************/
			for (int i = 0; i < nCol; i++) {
				if (N[i][i] != 1) {
					int temp = N[i][i];
					for (int z = 0; z < 2 * nCol; z++) {
						N[i][z] = gf.div(N[i][z], temp);
					}
				}
			}
			/************************************************************************/
			/** Get the new matrix. */
			/************************************************************************/

			int[][] CM = new int[nCol][nCol];
			for (int i = 0; i < nCol; i++) {
				for (int j = 0; j < nCol; j++) {
					CM[i][j] = N[i][j + nCol];
				}
			}
			byte[][] resultMatrix=intArray2ByteArray(CM);
			return resultMatrix;
		}
	}

	// 求秩
	public static int getRank(int[][] Mat, int nRow, int nCol) {
		// Define a variable to record the position of the main element.
		int[][] M = new int[nRow][nCol];
		for (int i = 0; i < nRow; ++i) {
			for (int j = 0; j < nCol; ++j) {
				M[i][j] = Mat[i][j];
			}
		}

		int yPos = 0;
		for (int i = 0; i < nRow; i++) {
			// Find the main element which must be non-zero.
			// 按列选取第一个非零元素作为主元素，然后交换所在行和主元素所在行
			boolean bFind = false;
			for (int x = yPos; x < nCol; x++) {
				for (int k = i; k < nRow; k++) {
					if (M[k][x] != 0) {
						// Exchange the two vectors.

						/** wx */
						if (k != i) {
							for (int m = 0; m < nCol; m++) {
								int nVal = M[i][m];
								M[i][m] = M[k][m];
								M[k][m] = nVal;
							}
						}
						/** wx */
						bFind = true;
						break;
					}
				}
				if (bFind == true) {
					yPos = x;
					break;
				}
				/** wx */
				// return -1;
				/** wx */
			}
			// 所在行位置以下的各行按序消元
			for (int j = i + 1; j < nRow; j++) {
				// Now, the main element must be nonsingular.
				int temp = gf.div(M[j][yPos], M[i][yPos]);
				for (int z = 0; z < nCol; z++) {
					M[j][z] = gf.add(M[j][z], gf.mul(temp, M[i][z]));// 模二加等价于模二减
				}
			}
			yPos++;
		}

		// The matrix becomes a scalar matrix. we need to make more elements
		// become 0 with elementary transformations.
		yPos = 0;
		for (int i = 1; i < nRow; i++) {
			for (int j = 0; j < nCol; j++) {
				if (M[i][j] != 0) {
					// the main element is found.
					yPos = j;
					break;
				}
			}
			for (int k = 0; k < i; k++) {
				int temp = gf.div(M[k][yPos], M[i][yPos]);
				for (int z = 0; z < nCol; z++) {
					M[k][z] = gf.add(M[k][z], gf.mul(temp, M[i][z]));
				}
			}
		}

		int nRank = 0;
		// Get the rank.
		for (int i = 0; i < nRow; i++) {
			int nNonzero = 0;
			for (int j = 0; j < nCol; j++) {
				if (M[i][j] != 0) {
					nNonzero++;
				}
			}
			// If there is only one nonzero element in the new matrix, it is
			// concluded an original packet is leaked.
			if (nNonzero > 0) {
				// Leaked.
				nRank++;
			}
		}
		return nRank;
	}


	// byte数组转化为int数组
	private static int[][] byteArray2IntArray(byte[][] b) {
		int row = b.length;
		int col = b[0].length;
		int[][] result = new int[row][col];
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				int temp = b[i][j];
				if (temp < 0) {
					result[i][j] = 256 - Math.abs(temp);
				} else {
					result[i][j] = temp;
				}
			}
		}
		return result;
	}

	// int数组转化为byte数组
	// 注意int不超过255   0到255之间
	private static byte[][] intArray2ByteArray(int[][] M) {
		int row = M.length;
		int col = M[0].length;
		byte[][] result = new byte[row][col];
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				result[i][j] = (byte) M[i][j];
			}
		}
		return result;
	}
}

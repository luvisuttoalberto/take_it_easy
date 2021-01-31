package takeiteasy.board;

import takeiteasy.tilepool.Tile;

public class BoardVanilla implements IBoard {

    private Tile[][] tileStorage = new Tile[5][5];

    public BoardVanilla() {
    }

    private Boolean areCoordinatesInRange(HexCoordinates coordinates){
        return (-3<coordinates.getX() && coordinates.getX()<3) &&
               (-3<coordinates.getY() && coordinates.getY()<3) &&
               (-3<coordinates.getZ() && coordinates.getZ()<3);
    }

    private Integer[] getStorageIdxsFromCoordinates(HexCoordinates coordinates){
        return new Integer[]{coordinates.getX()+2,coordinates.getY()+2};
    }

    @Override
    public void placeTile(Tile tile, HexCoordinates coordinates) throws OutOfBoardCoordinatesException, CoordinatesOccupidedException {
        if (!areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }

        Integer[] storageIdx = this.getStorageIdxsFromCoordinates(coordinates);
        if(this.tileStorage[storageIdx[0]][storageIdx[1]] != null){
            throw new CoordinatesOccupidedException();
        }

        this.tileStorage[storageIdx[0]][storageIdx[1]] = tile;

    }

    @Override
    public Tile getTile(HexCoordinates coordinates) throws OutOfBoardCoordinatesException {
        if (!this.areCoordinatesInRange(coordinates)) {
            throw new OutOfBoardCoordinatesException();
        }
        Integer[] storageIdx = this.getStorageIdxsFromCoordinates(coordinates);
        return this.tileStorage[storageIdx[0]][storageIdx[1]];
    }

    public enum RowOrientation{
        TOP,
        LEFT,
        RIGHT
    }

    private Integer getTileNumberAtOrientation(Tile tile, RowOrientation orientation){
        switch (orientation) {
            case LEFT -> {
                return tile.getLeft();
            }
            case RIGHT -> {
                return tile.getRight();
            }
            default ->{
                return tile.getTop();
            }
        }
    }

    private Tile getTileAtCounterRotatedCoordinates(HexCoordinates coordinates, RowOrientation counterRotation) throws OutOfBoardCoordinatesException {
        switch (counterRotation){
            case LEFT -> {
                return this.getTile(coordinates.rotateRight());
            }
            case RIGHT -> {
                return this.getTile(coordinates.rotateLeft());
            }
            default -> {
                return this.getTile(coordinates);
            }
        }
    }

    private Integer computeRowScore(Integer rowIndex,RowOrientation rowOrientation){

        // Get coordinates of first tile in the row
        Integer x0 = rowIndex,
                y0 = 2 - Math.max(0,x0),
                z0 = -2 - Math.min(x0,0) ;
        Integer rowLength = 5-Math.abs(rowIndex);

        Integer score = 0;
        try {

            // retrieve row number from first tile
            Tile tile = this.getTileAtCounterRotatedCoordinates(new HexCoordinates(x0, y0, z0), rowOrientation);
            Integer rowNumber = this.getTileNumberAtOrientation(tile, rowOrientation);

            // Scan tiles in row and check they all have the same number at given orientation
            for (int i = 0; i < rowLength; ++i) {

                Integer y = y0 - i;
                Integer z = z0 + i;
                tile = this.getTileAtCounterRotatedCoordinates(new HexCoordinates(x0, y, z), rowOrientation);
                Integer cellValue = this.getTileNumberAtOrientation(tile,rowOrientation);

                if (cellValue != rowNumber) {
                    // Differing value, no points
                    return 0;
                }
            }
            // compute score
            score = rowNumber * rowLength;
        }
        catch(Exception ignored){
        }

        return score;
    }

    @Override
    public Integer computeScore() {
        Integer score = 0;
        for (RowOrientation orientation : RowOrientation.values()) {
            for (Integer i=-2;i<3;++i){
                score += computeRowScore(i,orientation);
            }
        }
        return score;
    }

    private String stringifyTileContentAtStorageCoordinates(int i, int j){
        Tile res = tileStorage[i][j];
        if(res == null){
            return ".....";
        }
        return res.toString();
    }

    public void printBoard(){
        System.out.println("            |" + stringifyTileContentAtStorageCoordinates(2,0) + "|");
        for(int i = 0; i < 4; ++i){
            System.out.println("      |" + stringifyTileContentAtStorageCoordinates(1,i+1) + "|     |" +
                                           stringifyTileContentAtStorageCoordinates(3,i) + "|");
            if(i < 3){
                System.out.println("|" + stringifyTileContentAtStorageCoordinates(0,i+2) + "|     |" +
                                         stringifyTileContentAtStorageCoordinates(2,i+1) + "|     |" +
                                         stringifyTileContentAtStorageCoordinates(4,i) + "|");
            }
        }
        System.out.println("            |" + stringifyTileContentAtStorageCoordinates(2,4) + "|");
    }

    //DEBUG
    public void printMatrixBoard(){
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 5; j++){
                System.out.print(stringifyTileContentAtStorageCoordinates(i,j));
            }
            System.out.print("\n");
        }
    }
}

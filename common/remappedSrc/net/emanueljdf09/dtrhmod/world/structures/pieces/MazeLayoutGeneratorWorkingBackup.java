package net.emanueljdf09.dtrhmod.world.structures.pieces;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.world.structures.maze.MazeCell;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MazeLayoutGeneratorWorkingBackup {
    private static final int MODULE_SIZE = 7;

    public static void generateAndPlaceMaze(StructurePiecesBuilder collector, StructureTemplateManager templateManager, BlockPos originPos, RandomSource random) {
        DownTheRabbitHole.LOGGER.info("========== GENERATING MAZE ==========");
        int width = 21;
        int height = 21;

        MazeCell[][] grid = new MazeCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                grid[x][z] = new MazeCell(x, z);
            }
        }

        int centerX = width / 2;
        int centerZ = height / 2;

        for (int cx = centerX - 1; cx <= centerX + 1; cx++) {
            for (int cz = centerZ - 1; cz <= centerZ + 1; cz++) {
                grid[cx][cz].setType(MazeCell.Type.CENTER);
            }
        }

        int entranceX = centerX;
        int entranceZ = 0;
        grid[entranceX][entranceZ].setType(MazeCell.Type.ENTRANCE);

        MazeCell startCell = grid[centerX - 2][centerZ];
        connectCells(grid[centerX - 1][centerZ], startCell, -1, 0);

        connectCells(grid[entranceX][entranceZ], grid[entranceX][entranceZ + 1], 0, 1);

        carvePassages(grid, centerX - 2, centerZ, random);
        DownTheRabbitHole.LOGGER.info("========== MAZE CARVING FINISHED ==========");

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                MazeCell cell = grid[x][z];
                if (cell.getType() != MazeCell.Type.CENTER && cell.getType() != MazeCell.Type.ENTRANCE) {
                    cell.determinePieceTypeAndRotation();
                }
            }
        }

        int generatedCells = 0;
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                MazeCell cell = grid[x][z];
                if (cell.getType() != MazeCell.Type.EMPTY &&
                        cell.getType() != MazeCell.Type.CENTER &&
                        cell.getType() != MazeCell.Type.ENTRANCE) {
                    generatedCells++;
                }
            }
        }

        DownTheRabbitHole.LOGGER.info(
                "Maze generated {} path cells out of {}",
                generatedCells,
                width * height
        );

        placeCenterPiece(collector, templateManager, originPos, centerX - 1, centerZ - 1, "maze/center/center_nw_corner");
        placeCenterPiece(collector, templateManager, originPos, centerX, centerZ - 1, "maze/center/center_north");
        placeCenterPiece(collector, templateManager, originPos, centerX + 1, centerZ - 1, "maze/center/center_ne_corner");

        placeCenterPiece(collector, templateManager, originPos, centerX - 1, centerZ, "maze/center/center_west");
        placeCenterPiece(collector, templateManager, originPos, centerX, centerZ, "maze/center/center_middle");
        placeCenterPiece(collector, templateManager, originPos, centerX + 1, centerZ, "maze/center/center_east");

        placeCenterPiece(collector, templateManager, originPos, centerX - 1, centerZ + 1, "maze/center/center_sw_corner");
        placeCenterPiece(collector, templateManager, originPos, centerX, centerZ + 1, "maze/center/center_south");
        placeCenterPiece(collector, templateManager, originPos, centerX + 1, centerZ + 1, "maze/center/center_se_corner");

        BlockPos entranceWorldPos = originPos.offset(
                entranceX * MODULE_SIZE,
                0,
                entranceZ * MODULE_SIZE
        );
        collector.addPiece(new MazeStructurePiece(templateManager, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "maze/entrance_nwe"), entranceWorldPos, Rotation.NONE));

        for (int x = 0; x < width; x++) {
            for (int z = 0; z < height; z++) {
                MazeCell cell = grid[x][z];
                if (cell.getType() == MazeCell.Type.CENTER || cell.getType() == MazeCell.Type.ENTRANCE || cell.getType() == MazeCell.Type.EMPTY) {
                    continue;
                }

                String pathName = getPathTemplateName(cell);
                if (pathName == null) continue;

                BlockPos cellWorldPos = originPos.offset(
                        x * MODULE_SIZE,
                        0,
                        z * MODULE_SIZE
                );

                ResourceLocation templateId = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "maze/paths/" + pathName);
                collector.addPiece(new MazeStructurePiece(templateManager, templateId, cellWorldPos, Rotation.NONE));
            }
        }
    }

    private static void placeCenterPiece(StructurePiecesBuilder collector, StructureTemplateManager templateManager, BlockPos originPos, int absX, int absZ, String fullTemplatePath) {
        BlockPos pos = originPos.offset(
                absX * MODULE_SIZE,
                0,
                absZ * MODULE_SIZE
        );
        collector.addPiece(new MazeStructurePiece(templateManager, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, fullTemplatePath), pos, Rotation.NONE));
    }

    private static String getPathTemplateName(MazeCell cell) {
        switch (cell.getType()) {
            case STRAIGHT:
                if (cell.isNorthOpen() && cell.isSouthOpen()) return "straight_ns";
                return "straight_ew";

            case CORNER:
                if (cell.isNorthOpen() && cell.isEastOpen()) return "corner_ne";
                if (cell.isEastOpen() && cell.isSouthOpen()) return "corner_se";
                if (cell.isSouthOpen() && cell.isWestOpen()) return "corner_sw";
                return "corner_nw";

            case T_JUNCTION:
                if (!cell.isEastOpen()) return "t_nsw";
                if (!cell.isNorthOpen()) return "t_sew";
                if (!cell.isSouthOpen()) return "t_new";
                return "t_nse";

            case CROSSROAD:
                return "cross_nsew";

            case DEAD_END:
                if (cell.isNorthOpen()) return "deadend_n";
                if (cell.isSouthOpen()) return "deadend_s";
                if (cell.isEastOpen()) return "deadend_e";
                return "deadend_w";

            default:
                return null;
        }
    }

    private static void carvePassages(MazeCell[][] grid, int startX, int startZ, RandomSource random) {
        boolean[][] visited = new boolean[grid.length][grid[0].length];
 for (int x = 0; x < grid.length; x++) {
            for (int z = 0; z < grid[0].length; z++) {
                if (grid[x][z].getType() == MazeCell.Type.CENTER || grid[x][z].getType() == MazeCell.Type.ENTRANCE) {
                    visited[x][z] = true;
                }
            }
        }

        dfsCarve(grid, startX, startZ, visited, random);
    }

    private static void dfsCarve(MazeCell[][] grid, int x, int z, boolean[][] visited, RandomSource random) {
        visited[x][z] = true;

        int[][] directions = { {0, -1}, {1, 0}, {0, 1}, {-1, 0} };
        List<int[]> dirList = new ArrayList<>();
        for (int[] d : directions) {
            dirList.add(d);
        }
        Collections.shuffle(dirList, new java.util.Random(random.nextLong()));

        for (int[] d : dirList) {
            int nx = x + d[0];
            int nz = z + d[1];

            if (nx >= 0 && nx < grid.length && nz >= 0 && nz < grid[0].length && !visited[nx][nz]) {
                if (grid[nx][nz].getType() == MazeCell.Type.CENTER) continue;

                connectCells(grid[x][z], grid[nx][nz], d[0], d[1]);
                dfsCarve(grid, nx, nz, visited, random);
            }
        }
    }

    private static void connectCells(MazeCell c1, MazeCell c2, int dx, int dz) {
        if (dx == 0 && dz == -1) {
            c1.north = true;
            c2.south = true;
        } else if (dx == 1 && dz == 0) {
            c1.east = true;
            c2.west = true;
        } else if (dx == 0 && dz == 1) {
            c1.south = true;
            c2.north = true;
        } else if (dx == -1 && dz == 0) {
            c1.west = true;
            c2.east = true;
        }
    }
}

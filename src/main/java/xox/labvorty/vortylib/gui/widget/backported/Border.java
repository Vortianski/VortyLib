package xox.labvorty.vortylib.gui.widget.backported;

public class Border {
    public static class Single extends Border {
        private final int size;

        public Single(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }
    public static class All extends Border {
        private final int left;
        private final int top;
        private final int right;
        private final int bottom;

        public All(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }
    }
}

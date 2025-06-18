package dragrecyclerview;

public interface OnDragListener {

    void onMove(int fromPosition, int toPosition);

    void onSwiped(int position);

    void onDrop(int fromPosition, int toPosition);

}

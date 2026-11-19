package com.dataminde.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DiagramView extends View {
    private static final int BG = 0xFF101116;
    private static final int GRID = 0xFF242733;
    private static final int BLUE = 0xFF4778F5;
    private static final int TEXT = 0xFFF1F2F5;
    private static final int MUTED = 0xFF9AA1B2;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Node> nodes = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();
    private float scale = 1f;
    private float offsetX;
    private float offsetY;
    private float lastX;
    private float lastY;
    private Node draggedNode;
    private boolean draggingCanvas;
    private float downX;
    private float downY;
    private int nextId = 4;
    private final ScaleGestureDetector scaleDetector;

    public DiagramView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        float d = getResources().getDisplayMetrics().density;

        nodes.add(new Node(1, "Начало", "Старт схемы", 55 * d, 100 * d, 150 * d, 82 * d, 0xFF1C3155));
        nodes.add(new Node(2, "Процесс", "Основное действие", 275 * d, 230 * d, 160 * d, 82 * d, 0xFF30234D));
        nodes.add(new Node(3, "Результат", "Конечный блок", 505 * d, 100 * d, 150 * d, 82 * d, 0xFF183C35));
        connections.add(new Connection(1, 2));
        connections.add(new Connection(2, 3));

        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float oldScale = scale;
                scale = Math.max(0.35f, Math.min(2.8f, scale * detector.getScaleFactor()));
                float focusX = detector.getFocusX();
                float focusY = detector.getFocusY();
                offsetX = focusX - (focusX - offsetX) * (scale / oldScale);
                offsetY = focusY - (focusY - offsetY) * (scale / oldScale);
                invalidate();
                return true;
            }
        });
    }

    public void addNode() {
        float d = getResources().getDisplayMetrics().density;
        float x = (getWidth() / 2f - offsetX) / scale - 75 * d;
        float y = (getHeight() / 2f - offsetY) / scale - 40 * d;
        nodes.add(new Node(nextId++, "Новый блок", "Дважды нажми для выбора", x, y,
                155 * d, 82 * d, 0xFF26324C));
        invalidate();
    }

    public void resetCamera() {
        scale = 1f;
        offsetX = 0f;
        offsetY = 0f;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(BG);

        canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.scale(scale, scale);

        drawGrid(canvas);
        drawConnections(canvas);
        for (Node node : nodes) drawNode(canvas, node);

        canvas.restore();
    }

    private void drawGrid(Canvas canvas) {
        float step = dp(24);
        float left = (-offsetX / scale);
        float top = (-offsetY / scale);
        float right = left + getWidth() / scale;
        float bottom = top + getHeight() / scale;
        paint.setColor(GRID);
        paint.setStrokeWidth(1f / scale);

        float startX = (float) Math.floor(left / step) * step;
        float startY = (float) Math.floor(top / step) * step;
        for (float x = startX; x < right; x += step) canvas.drawLine(x, top, x, bottom, paint);
        for (float y = startY; y < bottom; y += step) canvas.drawLine(left, y, right, y, paint);
    }

    private void drawConnections(Canvas canvas) {
        for (Connection connection : connections) {
            Node a = findNode(connection.from);
            Node b = findNode(connection.to);
            if (a == null || b == null) continue;

            float x1 = a.x + a.w;
            float y1 = a.y + a.h / 2f;
            float x2 = b.x;
            float y2 = b.y + b.h / 2f;
            float mid = (x1 + x2) / 2f;

            Path path = new Path();
            path.moveTo(x1, y1);
            path.cubicTo(mid, y1, mid, y2, x2, y2);

            paint.setColor(BLUE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            canvas.drawPath(path, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(x1, y1, dp(4), paint);
            canvas.drawCircle(x2, y2, dp(4), paint);
        }
    }

    private void drawNode(Canvas canvas, Node node) {
        RectF rect = new RectF(node.x, node.y, node.x + node.w, node.y + node.h);
        paint.setColor(node.color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, dp(12), dp(12), paint);

        paint.setColor(node.selected ? BLUE : 0xFF46516B);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(node.selected ? dp(2) : dp(1));
        canvas.drawRoundRect(rect, dp(12), dp(12), paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(TEXT);
        paint.setTextSize(dp(14));
        paint.setTypeface(android.graphics.Typeface.create("sans-serif-medium", 0));
        canvas.drawText(node.title, node.x + dp(13), node.y + dp(31), paint);

        paint.setColor(MUTED);
        paint.setTextSize(dp(11));
        paint.setTypeface(android.graphics.Typeface.DEFAULT);
        canvas.drawText(node.subtitle, node.x + dp(13), node.y + dp(53), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                downX = x;
                downY = y;
                draggedNode = findNodeAt((x - offsetX) / scale, (y - offsetY) / scale);
                draggingCanvas = draggedNode == null;
                if (draggedNode != null) {
                    for (Node n : nodes) n.selected = false;
                    draggedNode.selected = true;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_DOWN:
                draggedNode = null;
                draggingCanvas = false;
                return true;

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) return true;
                float dx = x - lastX;
                float dy = y - lastY;
                if (draggedNode != null) {
                    draggedNode.x += dx / scale;
                    draggedNode.y += dy / scale;
                } else if (draggingCanvas) {
                    offsetX += dx;
                    offsetY += dy;
                }
                lastX = x;
                lastY = y;
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                draggedNode = null;
                draggingCanvas = false;
                performClick();
                return true;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private Node findNodeAt(float x, float y) {
        for (int i = nodes.size() - 1; i >= 0; i--) {
            Node n = nodes.get(i);
            if (x >= n.x && x <= n.x + n.w && y >= n.y && y <= n.y + n.h) return n;
        }
        return null;
    }

    private Node findNode(int id) {
        for (Node n : nodes) if (n.id == id) return n;
        return null;
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private static class Node {
        int id;
        String title;
        String subtitle;
        float x, y, w, h;
        int color;
        boolean selected;

        Node(int id, String title, String subtitle, float x, float y, float w, float h, int color) {
            this.id = id;
            this.title = title;
            this.subtitle = subtitle;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.color = color;
        }
    }

    private static class Connection {
        int from;
        int to;
        Connection(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
}

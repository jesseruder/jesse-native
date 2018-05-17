var currentTag = 2; // rootview has tag 1
function createView(type, args) {
    var tag = currentTag++;
    UI.createView(tag, type, JSON.stringify(args));
    return {
        tag: tag,
        type: type,
        setChildren: function(array) {
            UI.setChildren(tag, JSON.stringify(array));
        },
        setAsRootView: function() {
            UI.setChildren(1, JSON.stringify([tag]));
        },
        update: function(args) {
            UI.updateView(tag, type, JSON.stringify(args))
        }
    };
}

var rawText = createView("RCTRawText", {
    text: "Welcome!!!"
});

var text = createView("RCTText", {
    allowFontScaling: true,
    ellipsizeMode: 'tail',
    accessible: true,
    margin: 10,
    textAlign: 'center',
    fontSize: 20,
    color: -13421773
});

text.setChildren([rawText.tag]);

var container = createView("RCTView", {
    backgroundColor: -321,
    justifyContent: 'center',
    alignItems: 'center',
    jesseGesture: 'hello',
    flex: 1
});

container.setChildren([text.tag]);
container.setAsRootView();

UI.onBatchComplete();

function hello(position) {
    rawText.update({
        text: JSON.stringify(position)
    });
    UI.onBatchComplete();
}
